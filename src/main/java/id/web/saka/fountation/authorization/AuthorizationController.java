package id.web.saka.fountation.authorization;

import id.web.saka.fountation.authorization.role.permission.RolePermissionDTO;
import id.web.saka.fountation.authorization.role.permission.RolePermissionService;
import id.web.saka.fountation.authorization.user.UserRoleService;
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
public class AuthorizationController {

    Logger log = LoggerFactory.getLogger(AuthorizationController.class);

    private final UserRoleService userRoleService;

    private final RolePermissionService rolePermissionService;


    public AuthorizationController(UserRoleService userRoleService, RolePermissionService rolePermissionService) {
        this.userRoleService = userRoleService;
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/authorization/role/permission/detail/{companyId}/{userId}")
    public Mono<RolePermissionDTO> getRoleByUserId(@PathVariable("companyId") Long companyId, @PathVariable("userId") Long userId) {

        log.info("Fetching RolePermissionDTO for companyId: " + companyId + ", userId: " + userId);

        return userRoleService.getRoleByCompanyIdAndUserId(companyId, userId)
                .flatMap(role ->
                        rolePermissionService.getPermissionsByRoleId(role.getId())
                        .collectList()
                        .map(permissions -> new RolePermissionDTO(role, permissions))
                )
                .doOnNext(rolePermissionDTO ->
                        log.info("Fetched RolePermissionDTO: " + rolePermissionDTO.toString())
                );
    }

    @GetMapping("/authorization/role/permission/list/{companyId}")
    public Flux<RolePermissionDTO> getRolesPermissionsByUserId(@PathVariable("companyId") Long companyId) {

        return userRoleService.getRolesByCompanyId(companyId)
                .flatMap(role ->
                        rolePermissionService.getPermissionsByRoleId(role.getId())
                                .collectList()
                                .map(permissions -> new RolePermissionDTO(role, permissions))
                );
    }


}
