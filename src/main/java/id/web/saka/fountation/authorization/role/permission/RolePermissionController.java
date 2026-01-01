package id.web.saka.fountation.authorization.role.permission;

import id.web.saka.fountation.authorization.permission.PermissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class RolePermissionController {
    Logger log = LoggerFactory.getLogger(RolePermissionController.class);

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/authorization/role/permission/detail/{companyId}/{roleId}")
    public Mono<RolePermissionDTO> getRolePermissionsByRoleId(@PathVariable Long companyId, @PathVariable Long roleId) {
        return rolePermissionService.getRolePermissionsByRoleId(companyId, roleId);
    }


    /*@GetMapping("/authorization/role/permissionOnly/list/{roleId}")
    public Flux<PermissionDTO> getAllPermissionsByRoleId(@PathVariable Long roleId) {
        log.info("Fetching all permissions for roleId: " + roleId);

        return rolePermissionService.getPermissionsForRole(roleId);
    }*/

}
