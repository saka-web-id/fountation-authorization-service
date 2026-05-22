package id.web.saka.fountation.authorization.company.role.permission;

import id.web.saka.fountation.authorization.company.CompanyRoleService;
import id.web.saka.fountation.authorization.permission.PermissionDTO;
import id.web.saka.fountation.authorization.permission.PermissionService;
import id.web.saka.fountation.authorization.role.RoleMapper;
import id.web.saka.fountation.authorization.role.RoleService;
import id.web.saka.fountation.configbase.fountation.FountationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompanyRolePermissionService {

    private static final Logger log = LoggerFactory.getLogger(CompanyRolePermissionService.class);

    private final CompanyRolePermissionRepository companyRolePermissionRepository;

    private final PermissionService permissionService;

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    private final CompanyRoleService companyRoleService;

    private final ReactiveRedisTemplate<String, CompanyRolePermissionDTO> redisTemplateCompanyRolePermissionDTO;

    private final FountationProperties fountationProperties;

    public CompanyRolePermissionService(CompanyRolePermissionRepository companyRolePermissionRepository,
                                 RoleService roleService,
                                 RoleMapper roleMapper,
                                 PermissionService permissionService,
                                 CompanyRoleService companyRoleService,
                                 ReactiveRedisTemplate<String, CompanyRolePermissionDTO> redisTemplateCompanyRolePermissionDTO,
                                 FountationProperties fountationProperties) {
        this.companyRolePermissionRepository = companyRolePermissionRepository;
        this.roleService = roleService;
        this.roleMapper = roleMapper;
        this.permissionService = permissionService;
        this.companyRoleService = companyRoleService;
        this.redisTemplateCompanyRolePermissionDTO = redisTemplateCompanyRolePermissionDTO;
        this.fountationProperties = fountationProperties;
    }

    public Flux<PermissionDTO> getPermissionsByCompanyIdRoleId(Long companyId, Long roleId) {
        return companyRolePermissionRepository.findAllByCompanyIdAndRoleId(companyId, roleId)
                .flatMap(rolePermission ->
                        permissionService.getPermissionById(rolePermission.getPermissionId())
                );
    }

    public Mono<CompanyRolePermissionDTO> getCompanyRolePermissionsByCompanyRoleId(Long companyId, Long roleId) {
        log.info("[COMPANY_ROLE_PERMISSION] Fetch | START | companyId={} roleId={}", companyId, roleId);

        return redisTemplateCompanyRolePermissionDTO.opsForValue().get(buildCacheKey(companyId, roleId))
                .map(obj -> {
                    log.info("[REDIS] Cache | HIT | key={}", buildCacheKey(companyId, roleId));
                    return (CompanyRolePermissionDTO) obj;
                })
                .onErrorResume(e -> {
                    log.warn("[REDIS] Cache | ERROR | msg={} | fallback=DB", e.getMessage());
                    return Mono.empty();
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            log.info("[REDIS] Cache | MISS | key={} | fetching from DB", buildCacheKey(companyId, roleId));
                            return roleService.getRoleById(roleId).flatMap(role ->
                                    companyRolePermissionRepository.findAllByCompanyIdAndRoleId(companyId, roleId)
                                            .collectList()
                                            .flatMap(companyRolePermissions ->
                                                    getPermissionsForRole(companyId, roleId)
                                                            .collectList()
                                                            .flatMap(permissionDTOS ->
                                                                    cacheCompanyRolePermissionDTO(buildCacheKey(companyId, roleId), new CompanyRolePermissionDTO(
                                                                            roleId,
                                                                            companyId,
                                                                            role.getName().toString(),
                                                                            role.getDescription(),
                                                                            permissionDTOS
                                                                    ))
                                                            )
                                            )
                            );
                        })
                );
    }

    public Mono<CompanyRolePermissionDTO> updateRolePermissions(Long companyId, Long roleId, CompanyRolePermissionDTO rolePermissionDTO) {
        log.info("[COMPANY_ROLE_PERMISSION] Update | START | companyId={} roleId={}", companyId, roleId);

        return roleService.saveRole(roleMapper.toRequestEntity(rolePermissionDTO))
                .flatMap(savedRole -> saveRolePermission(companyId, roleId, rolePermissionDTO))
                .flatMap(dto -> {
                    return cacheCompanyRolePermissionDTO(buildCacheKey(companyId, roleId), dto)
                            .onErrorResume(err -> {
                                log.warn("[REDIS] Cache | ERROR | action=UPDATE msg={}", err.getMessage());
                                return Mono.empty();
                            })
                            .thenReturn(dto);
                })
                .doOnSuccess(v -> log.info("[COMPANY_ROLE_PERMISSION] Update | SUCCESS | companyId={} roleId={}", companyId, roleId));
    }

    private Mono<CompanyRolePermissionDTO> saveRolePermission(Long companyId, Long roleId, CompanyRolePermissionDTO rolePermissionDTO) {
        return companyRolePermissionRepository.deleteAllByRoleId(roleId)
                .then(
                        Flux.fromIterable(rolePermissionDTO.permissions())
                                .flatMap(permissionDTO -> {
                                    if (permissionDTO.isAssigned()) {
                                        CompanyRolePermission companyRolePermission = new CompanyRolePermission();
                                        companyRolePermission.setRoleId(roleId);
                                        companyRolePermission.setCompanyId(companyId);
                                        companyRolePermission.setPermissionId(permissionDTO.id());
                                        return companyRolePermissionRepository.save(companyRolePermission);
                                    } else {
                                        return Mono.empty();
                                    }
                                })
                                .collectList()
                                .thenReturn(rolePermissionDTO)
                )
                .flatMap(dto ->
                        cacheCompanyRolePermissionDTO(buildCacheKey(companyId, roleId), dto)
                                .onErrorResume(err -> {
                                    log.warn("[REDIS] Cache | ERROR | action=SAVE msg={}", err.getMessage());
                                    return Mono.just(dto);
                                })
                );
    }


    private Flux<PermissionDTO> getPermissionsForRole(Long companyId, Long roleId) {
        Mono<Set<Long>> assignedIdsMono = getPermissionsByCompanyIdRoleId(companyId, roleId)
                .map(PermissionDTO::id)
                .collect(Collectors.toSet());

        return assignedIdsMono.flatMapMany(assignedIds ->
                companyRoleService.getAllRolesByCompanyId(companyId)
                        .flatMap(roleDTO -> getPermissionsByCompanyIdRoleId(companyId, roleDTO.getId()))
                        .distinct(PermissionDTO::id)
                        .map(permissionDTO ->
                                new PermissionDTO(
                                        permissionDTO.id(),
                                        permissionDTO.name(),
                                        permissionDTO.superAdmin(),
                                        permissionDTO.resource(),
                                        permissionDTO.action(),
                                        permissionDTO.description(),
                                        assignedIds.contains(permissionDTO.id())
                                )
                        )
        );
    }

    public Mono<Void> setupPermissionsForRole(Long companyId, Long roleId) {
        log.info("[COMPANY_ROLE_PERMISSION] Setup | START | companyId={} roleId={}", companyId, roleId);
        return permissionService.findAll()
                .filter(permissionDTO -> !permissionDTO.superAdmin())
                .flatMap(permissionDTO -> {
                    CompanyRolePermission rolePermission = new CompanyRolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setCompanyId(companyId);
                    rolePermission.setPermissionId(permissionDTO.id());
                    return companyRolePermissionRepository.save(rolePermission);
                })
                .then()
                .doOnSuccess(v -> log.info("[COMPANY_ROLE_PERMISSION] Setup | SUCCESS | companyId={} roleId={}", companyId, roleId));
    }

    private Mono<CompanyRolePermissionDTO> cacheCompanyRolePermissionDTO(String key, CompanyRolePermissionDTO dto) {
        log.info("[REDIS] Cache | SET | key={}", key);

        return redisTemplateCompanyRolePermissionDTO.opsForValue()
                .set(key, dto, Duration.ofMinutes(fountationProperties.getService().getRedis().getStore().getDuration().getMinutes()))
                .onErrorResume(err -> {
                    log.warn("[REDIS] Cache | ERROR | action=SET msg={}", err.getMessage());
                    return Mono.empty();
                })
                .thenReturn(dto);
    }

    private String buildCacheKey(Long companyId, Long roleId) {
        return "companyRolePermissionDTO:companyId:" + companyId + ":roleId:" + roleId;
    }
}
