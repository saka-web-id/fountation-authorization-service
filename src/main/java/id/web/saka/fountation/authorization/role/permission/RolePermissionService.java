package id.web.saka.fountation.authorization.role.permission;

import id.web.saka.fountation.authorization.company.CompanyRoleService;
import id.web.saka.fountation.authorization.permission.PermissionDTO;
import id.web.saka.fountation.authorization.permission.PermissionService;
import id.web.saka.fountation.authorization.role.RoleMapper;
import id.web.saka.fountation.authorization.role.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RolePermissionService {

    Logger log = LoggerFactory.getLogger(RolePermissionService.class);

    private final RolePermissionRepository rolePermissionRepository;

    private final PermissionService permissionService;

    private final RoleService  roleService;

    private final RoleMapper roleMapper;

    private final CompanyRoleService companyRoleService;

    public RolePermissionService(RolePermissionRepository rolePermissionRepository,
                                 RoleService roleService,
                                 RoleMapper roleMapper,
                                 PermissionService permissionService,
                                 CompanyRoleService companyRoleService) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleService = roleService;
        this.roleMapper = roleMapper;
        this.permissionService = permissionService;
        this.companyRoleService = companyRoleService;
    }

    public Flux<PermissionDTO> getPermissionsByRoleId(Long roleId) {

        return rolePermissionRepository.findAllByRoleId(roleId)
                .flatMap(rolePermission ->
                        permissionService.getPermissionById(rolePermission.getPermissionId())
                ).doOnNext(permissionDTO ->
                        {
                            // You can add logging here if needed
                            log.info("Fetched PermissionDTO: " + permissionDTO.toString());
                        }
                );

    }

    public Mono<RolePermissionDTO> getRolePermissionsByRoleId(Long companyId, Long roleId) {
        log.info("getRolePermissionByRoleId: {}", roleId);

        return roleService.getRoleById(roleId)
                .flatMap(role ->
                        getPermissionsForRole(companyId, roleId)
                                .collectList()
                                .map(permissionDTOS -> new RolePermissionDTO(role, permissionDTOS)));
    }

    public Mono<RolePermissionDTO> updateRolePermissions(Long roleId, RolePermissionDTO rolePermissionDTO) {
        log.info("updateRolePermissions|roleId:{}|rolePermissionDTO:{}|START", roleId, rolePermissionDTO);

        return roleService.saveRole(roleMapper.toRequestEntity(rolePermissionDTO))
                .flatMap(savedRole -> saveRolePermission(roleId, rolePermissionDTO));
    }

    public Mono<RolePermissionDTO> addRolePermissions(Long companyId, RolePermissionDTO rolePermissionDTOMono) {
        return roleService.saveRole(roleMapper.toRequestEntity(rolePermissionDTOMono))
                        .flatMap(savedRole ->
                                companyRoleService.saveCompanyRole(companyId, savedRole)
                                        .flatMap(savedCompanyRole -> saveRolePermission(savedCompanyRole.getRoleId(), rolePermissionDTOMono))
                        );
    }

    private Mono<RolePermissionDTO> saveRolePermission(Long roleId, RolePermissionDTO rolePermissionDTO) {
        return rolePermissionRepository.deleteAllByRoleId(roleId)
                .then(
                        Flux.fromIterable(rolePermissionDTO.getPermissions())
                                .flatMap(permissionDTO -> {
                                    if (permissionDTO.isAssigned()) {
                                        RolePermission rolePermission = new RolePermission();
                                        rolePermission.setRoleId(roleId);
                                        rolePermission.setPermissionId(permissionDTO.getId());
                                        return rolePermissionRepository.save(rolePermission);
                                    } else {
                                        return Mono.empty();
                                    }
                                })
                                .collectList()
                                .thenReturn(rolePermissionDTO) // ✅ return the original DTO without blocking
                );
    }


    private Flux<PermissionDTO> getPermissionsForRole(Long companyId, Long roleId) {
        // Step 1: collect assigned permission IDs into a Set
        Mono<Set<Long>> assignedIdsMono = getPermissionsByRoleId(roleId)
                .map(PermissionDTO::getId)
                .collect(Collectors.toSet());

        // Then map all permissions with flag and ensure uniqueness
        return assignedIdsMono.flatMapMany(assignedIds ->
                companyRoleService.getAllRolesByCompanyId(companyId)
                        .flatMap(roleDTO -> getPermissionsByRoleId(roleDTO.getId()))
                        .distinct(PermissionDTO::getId) // Ensure unique PermissionDTOs based on ID
                        .map(permissionDTO -> {
                            permissionDTO.setAssigned(assignedIds.contains(permissionDTO.getId()));
                            return permissionDTO;
                        })
        );
    }
}
