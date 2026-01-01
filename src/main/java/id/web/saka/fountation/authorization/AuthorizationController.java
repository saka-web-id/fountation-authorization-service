package id.web.saka.fountation.authorization;

import id.web.saka.fountation.authorization.role.permission.RolePermissionDTO;
import id.web.saka.fountation.authorization.role.permission.RolePermissionService;
import id.web.saka.fountation.authorization.user.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class AuthorizationController {

    Logger log = LoggerFactory.getLogger(AuthorizationController.class);

    private final UserRoleService userRoleService;

    private final RolePermissionService rolePermissionService;


    public AuthorizationController(UserRoleService userRoleService, RolePermissionService rolePermissionService) {
        this.userRoleService = userRoleService;
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/authorization/role/permission/detail/byUserId/{userId}")
    public Mono<RolePermissionDTO> getRoleByUserId(@PathVariable("userId") Long userId) {

        log.info("Fetching RolePermissionDTO for userId: " + userId);
        return userRoleService.getRoleByUserId(userId)
                .flatMap(role ->
                        rolePermissionService.getPermissionsByRoleId(role.getId())
                        .collectList()
                        .map(permissions -> new RolePermissionDTO(role, permissions))
                )
                .doOnNext(rolePermissionDTO ->
                        log.info("Fetched RolePermissionDTO: " + rolePermissionDTO.toString())
                );
    }

    @PostMapping("/authorization/role/permission/update/{roleId}")
    public Mono<ResponseEntity<RolePermissionDTO>> updateRolePermissions(@PathVariable Long roleId, @RequestBody Mono<RolePermissionDTO> payload) {
        log.info("updateRolePermissions|roleId:{}|START", roleId);

        return payload.flatMap(rolePermissionDTO -> rolePermissionService.updateRolePermissions(roleId, rolePermissionDTO)).map(ResponseEntity::ok)
                .doOnError(e -> log.error("Failed to update role permissions for roleId {}", roleId, e))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PostMapping("/authorization/role/permission/add/{companyId}")
    public Mono<ResponseEntity<RolePermissionDTO>> saveRolePermissions(@PathVariable Long companyId, @RequestBody Mono<RolePermissionDTO> payload) {
        log.info("saveRolePermissions|companyId:{}|START", companyId);

        return payload.flatMap(rolePermissionDTO -> {
                    rolePermissionDTO.setRoleId(null); //need to set null since want to add new record
                    return rolePermissionService.addRolePermissions(companyId, rolePermissionDTO);
                })
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Failed to update role permissions for companyId:{}", companyId, e))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}
