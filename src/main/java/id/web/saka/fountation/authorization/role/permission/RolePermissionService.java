package id.web.saka.fountation.authorization.role.permission;

import id.web.saka.fountation.authorization.permission.Permission;
import id.web.saka.fountation.authorization.permission.PermissionDTO;
import id.web.saka.fountation.authorization.permission.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class RolePermissionService {

    Logger log = LoggerFactory.getLogger(RolePermissionService.class);

    private final RolePermissionRepository rolePermissionRepository;

    private final PermissionService permissionService;

    public RolePermissionService(RolePermissionRepository rolePermissionRepository, PermissionService permissionService) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionService = permissionService;
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
}
