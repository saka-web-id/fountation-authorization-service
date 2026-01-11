package id.web.saka.fountation.authorization;

import id.web.saka.fountation.authorization.policy.PolicyService;
import id.web.saka.fountation.authorization.role.permission.RolePermissionDTO;
import id.web.saka.fountation.authorization.role.permission.RolePermissionService;
import id.web.saka.fountation.authorization.user.role.UserRoleService;
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


    /*@GetMapping("/authorization/role/permission/detail/byUserId/{userId}")
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
    }*/



}
