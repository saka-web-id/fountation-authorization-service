package id.web.saka.fountation.authorization.policy;

import id.web.saka.fountation.authorization.company.role.permission.CompanyRolePermissionService;
import id.web.saka.fountation.authorization.role.Role;
import id.web.saka.fountation.authorization.user.UserService;
import id.web.saka.fountation.authorization.user.role.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
public class PolicyService {

    Logger logger = LoggerFactory.getLogger(PolicyService.class);

    private final UserService userService;

    private final CompanyRolePermissionService rolePermissionService;

    private final UserRoleService userRoleService;

    public PolicyService(UserService userService, UserRoleService userRoleService, CompanyRolePermissionService rolePermissionService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.rolePermissionService = rolePermissionService;
    }


    public Mono<PolicyResponseDTO> evaluate(Jwt jwt, Long userId, Long companyId, PolicyRequestDTO request) {
        logger.info("evaluate|request: {}", request);

        Mono<Long> userIdDecision = userId != null
                ? Mono.just(userId)
                : userService.getUserIdByEmail(jwt.getClaimAsString("https://example.com/email"))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found in system")));

        return userIdDecision
                .flatMap(uid -> userRoleService.getRoleByUserIdandCompanyId(uid, companyId)
                        .flatMap(userRole -> {
                            logger.info("Checking role: {} (ID: {})", userRole.getName(), userRole.getId());

                            // 1. SUPER_ADMIN BYPASS
                            // Check by ID (1) or by Name string - whichever matches your DB/Enum
                            if (userRole.getName().equals(Role.RoleName.SUPER_ADMIN)) {
                                logger.info("SUPER_ADMIN detected for UID: {}. Granting full access.", uid);
                                return Mono.just(new PolicyResponseDTO(true, "Allowed: Super Admin Privilege"));
                            }

                            // 2. STANDARD PERMISSION CHECK
                            return rolePermissionService.getPermissionsByCompanyIdRoleId(companyId, userRole.getId())
                                    .filter(p -> request.action().equalsIgnoreCase(p.action()) &&
                                            request.resource().toLowerCase().startsWith(p.resource().toLowerCase()))
                                    .next()
                                    .map(p -> new PolicyResponseDTO(true, "Allowed by role " + userRole.getName()))
                                    .defaultIfEmpty(new PolicyResponseDTO(false, "Denied: No matching permission"));
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            logger.warn("Access Denied: UID {} has no role in CID {}", uid, companyId);
                            return Mono.just(new PolicyResponseDTO(false, "Denied: No assigned role"));
                        }))
                )
                .onErrorResume(e -> {
                    logger.error("Policy evaluation crashed: {}", e.getMessage());
                    return Mono.just(new PolicyResponseDTO(false, "Denied: Internal error"));
                })
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(TimeoutException.class, e -> Mono.just(new PolicyResponseDTO(false, "Denied: Timeout")));
    }






}
