package id.web.saka.fountation.authorization.policy;

import id.web.saka.fountation.authorization.permission.PermissionDTO;
import id.web.saka.fountation.authorization.role.permission.RolePermissionService;
import id.web.saka.fountation.authorization.user.UserService;
import id.web.saka.fountation.authorization.user.role.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PolicyService {

    Logger logger = LoggerFactory.getLogger(PolicyService.class);

    private final UserService userService;

    private final RolePermissionService rolePermissionService;

    private final UserRoleService userRoleService;

    public PolicyService(UserService userService, UserRoleService userRoleService, RolePermissionService rolePermissionService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.rolePermissionService = rolePermissionService;
    }


    public Mono<PolicyResponseDTO> evaluate(Jwt jwt, Long userId, Long companyId, PolicyRequestDTO request) {
        logger.info("evaluate|request:{}", request.toString());

        Mono<Long> userIdDecision = userId != null
                ? Mono.just(userId)
                : userService.getUserIdByEmail(jwt.getClaimAsString("https://example.com/email"));

        return userIdDecision.flatMap(uid ->
                userRoleService.getRoleByUserIdandCompanyId(uid, companyId)
                        .flatMap(userRole ->
                                rolePermissionService.getPermissionsByRoleId(userRole.getId())
                                        .map(PermissionDTO::getName) // extract action from each PermissionDTO
                                        .filter(action -> action.equalsIgnoreCase(request.action())) // check match
                                        .hasElements() // returns Mono<Boolean>
                                        .map(hasPermission -> {

                                            //TODO For testing purpose all is allowed
                                            logger.info("evaluate result|request:{}|hasPermission:{}", request.toString(), hasPermission);
                                            return new PolicyResponseDTO(true, "Allowed by role " + userRole.getName());
                                            /*if (hasPermission) {
                                                return new PolicyResponseDTO(true, "Allowed by role " + userRole.getName());
                                            } else {
                                                return new PolicyResponseDTO(false, "Denied: action not permitted by role " + userRole.getName());
                                            }*/
                                        })
                        )
        );
    }




}
