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

    Logger log = LoggerFactory.getLogger(CompanyRolePermissionService.class);

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
                ).doOnNext(permissionDTO ->
                        {
                            // You can add logging here if needed
                            log.info("Fetched PermissionDTO: " + permissionDTO.toString());
                        }
                );

    }

    public Mono<CompanyRolePermissionDTO> getCompanyRolePermissionsByCompanyRoleId(Long companyId, Long roleId) {
        log.info("getRolePermissionByRoleId: {}", roleId);

        return redisTemplateCompanyRolePermissionDTO.opsForValue().get(buildCacheKey(companyId, roleId))
                .map(obj -> (CompanyRolePermissionDTO) obj)   // cast here
                .onErrorResume(e -> {
                    log.warn("Redis unavailable, fallback to DB: {}", e.getMessage());
                    return Mono.empty();
                })
                .switchIfEmpty(
                        roleService.getRoleById(roleId).flatMap(role ->
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
                        )
                );
    }

    public Mono<CompanyRolePermissionDTO> updateRolePermissions(Long companyId, Long roleId, CompanyRolePermissionDTO rolePermissionDTO) {
        log.info("updateRolePermissions|roleId:{}|rolePermissionDTO:{}|START", roleId, rolePermissionDTO);

        return roleService.saveRole(roleMapper.toRequestEntity(rolePermissionDTO))
                .flatMap(savedRole -> saveRolePermission(companyId, roleId, rolePermissionDTO))
                .flatMap(dto -> {
                    // chain cache write, but return dto immediately
                    return cacheCompanyRolePermissionDTO(buildCacheKey(companyId, roleId), dto)
                            .onErrorResume(err -> {
                                log.warn("Failed to cache in Redis: {}", err.getMessage());
                                return Mono.empty(); // ignore cache failure
                            })
                            .thenReturn(dto); // return original dto regardless
                });



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
                                .thenReturn(rolePermissionDTO) // ✅ return the original DTO without blocking
                )
                .flatMap(dto ->
                        cacheCompanyRolePermissionDTO(buildCacheKey(companyId, roleId), dto)
                                .onErrorResume(err -> {
                                    log.warn("Failed to cache in Redis: {}", err.getMessage());
                                    return Mono.just(dto); // fallback to original DTO if cache fails
                                })
                );
    }


    private Flux<PermissionDTO> getPermissionsForRole(Long companyId, Long roleId) {
        // Step 1: collect assigned permission IDs into a Set
        Mono<Set<Long>> assignedIdsMono = getPermissionsByCompanyIdRoleId(companyId, roleId)
                .map(PermissionDTO::id)
                .collect(Collectors.toSet());

        // Step 2: map all permissions with flag and ensure uniqueness
        return assignedIdsMono.flatMapMany(assignedIds ->
                companyRoleService.getAllRolesByCompanyId(companyId)
                        .flatMap(roleDTO -> getPermissionsByCompanyIdRoleId(companyId, roleDTO.getId()))
                        .distinct(PermissionDTO::id) // Ensure unique PermissionDTOs based on ID
                        .map(permissionDTO ->
                                new PermissionDTO(
                                        permissionDTO.id(),
                                        permissionDTO.name(),
                                        permissionDTO.superAdmin(),
                                        permissionDTO.resource(),
                                        permissionDTO.action(),
                                        permissionDTO.description(),
                                        assignedIds.contains(permissionDTO.id()) // updated flag
                                )
                        )
        );
    }

    public Mono<Void> setupPermissionsForRole(Long companyId, Long roleId) {
        //TODO setup permissions for role except for permissions with flag is_super_admin is true
        return permissionService.findAll()
                .filter(permissionDTO -> !permissionDTO.superAdmin())
                .flatMap(permissionDTO -> {
                    CompanyRolePermission rolePermission = new CompanyRolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setCompanyId(companyId);
                    rolePermission.setPermissionId(permissionDTO.id());
                    return companyRolePermissionRepository.save(rolePermission);
                })
                .then();
    }

    private Mono<CompanyRolePermissionDTO> cacheCompanyRolePermissionDTO(String key, CompanyRolePermissionDTO dto) {
        log.info("Redis cache user {} with dto {} ", key, dto.toString() );

        return redisTemplateCompanyRolePermissionDTO.opsForValue()
                .set(key, dto, Duration.ofMinutes(fountationProperties.getService().getRedis().getStore().getDuration().getMinutes()))//fountation.service.redis.store.duration.minutes
                .onErrorResume(err -> {
                    log.warn("Failed to cache in Redis: {}", err.getMessage());
                    return Mono.empty();
                })
                .thenReturn(dto);
    }

    private String buildCacheKey(Long companyId, Long roleId) {
        return "companyRolePermissionDTO:companyId:" + companyId + ":roleId:" + roleId;
    }
}
