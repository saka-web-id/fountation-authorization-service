package id.web.saka.fountation.authorization.company.role.permission;

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
public class CompanyRolePermissionService {

    Logger log = LoggerFactory.getLogger(CompanyRolePermissionService.class);

    private final CompanyRolePermissionRepository companyRolePermissionRepository;

    private final PermissionService permissionService;

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    private final CompanyRoleService companyRoleService;

    public CompanyRolePermissionService(CompanyRolePermissionRepository companyRolePermissionRepository,
                                 RoleService roleService,
                                 RoleMapper roleMapper,
                                 PermissionService permissionService,
                                 CompanyRoleService companyRoleService) {
        this.companyRolePermissionRepository = companyRolePermissionRepository;
        this.roleService = roleService;
        this.roleMapper = roleMapper;
        this.permissionService = permissionService;
        this.companyRoleService = companyRoleService;
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

        return roleService.getRoleById(roleId).flatMap(role -> {
            return companyRolePermissionRepository.findAllByCompanyIdAndRoleId(companyId, roleId)
                    .collectList()
                    .flatMap(companyRolePermissions ->
                            getPermissionsForRole(companyId, roleId)
                                    .collectList()
                                    .map(permissionDTOS ->
                                            new CompanyRolePermissionDTO(
                                                    roleId,
                                                    companyId,
                                                    role.getName().toString(), // fill in role name if needed
                                                    role.getDescription(), // fill in description if needed
                                                    permissionDTOS
                                            )
                                    )
                    );
        });


    }

    public Mono<CompanyRolePermissionDTO> updateRolePermissions(Long companyId, Long roleId, CompanyRolePermissionDTO rolePermissionDTO) {
        log.info("updateRolePermissions|roleId:{}|rolePermissionDTO:{}|START", roleId, rolePermissionDTO);

        return roleService.saveRole(roleMapper.toRequestEntity(rolePermissionDTO))
                .flatMap(savedRole -> saveRolePermission(companyId, roleId, rolePermissionDTO));
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
                                        permissionDTO.isSuperAdmin(),
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
                .filter(permissionDTO -> !permissionDTO.isSuperAdmin())
                .flatMap(permissionDTO -> {
                    CompanyRolePermission rolePermission = new CompanyRolePermission();
                    rolePermission.setRoleId(roleId);
                    rolePermission.setCompanyId(companyId);
                    rolePermission.setPermissionId(permissionDTO.id());
                    return companyRolePermissionRepository.save(rolePermission);
                })
                .then();
    }
}
