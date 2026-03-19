package id.web.saka.fountation.authorization.policy;

import id.web.saka.fountation.authorization.company.role.permission.CompanyRolePermissionService;
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

        // 1. Resolve User ID
        Mono<Long> userIdDecision = userId != null
                ? Mono.just(userId)
                : userService.getUserIdByEmail(jwt.getClaimAsString("https://example.com/email"))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found in system")));

        return userIdDecision
                .flatMap(uid -> userRoleService.getRoleByUserIdandCompanyId(uid, companyId)
                        // 2. Handle User Role
                        .flatMap(userRole -> {
                            logger.info("Checking permissions for role: {} (ID: {})", userRole.getName(), userRole.getId());

                            return rolePermissionService.getPermissionsByCompanyIdRoleId(companyId, userRole.getId())
                                    // 3. Filter for specific permission
                                    .filter(permissionDTO ->
                                            request.action().equalsIgnoreCase(permissionDTO.action()) &&
                                                    request.resource().toLowerCase().startsWith(permissionDTO.resource().toLowerCase())
                                    )
                                    .next() // Take the first match
                                    .map(permissionDTO -> {
                                        logger.info("Permission matched: {}", permissionDTO.resource());
                                        return new PolicyResponseDTO(true, "Allowed by role " + userRole.getName());
                                    })
                                    // If permissions exist but none matched the filter
                                    .defaultIfEmpty(new PolicyResponseDTO(false, "Denied: No matching permission for role " + userRole.getName()));
                        })
                        // 4. Handle Case: User has NO role in this company
                        .switchIfEmpty(Mono.defer(() -> {
                            logger.warn("Access Denied: UserId {} has no role in CompanyId {}", uid, companyId);
                            return Mono.just(new PolicyResponseDTO(false, "Denied: User has no assigned role in this company"));
                        }))
                )
                // 5. Global Error Handling (Prevents gRPC hangs on exceptions)
                .onErrorResume(e -> {
                    logger.error("Policy evaluation crashed: {}", e.getMessage());
                    return Mono.just(new PolicyResponseDTO(false, "Denied: Internal evaluation error"));
                })
                // 6. Safety Timeout
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(TimeoutException.class, e -> {
                    logger.error("Policy evaluation timed out after 10s");
                    return Mono.just(new PolicyResponseDTO(false, "Denied: Evaluation timeout"));
                });
    }






}
