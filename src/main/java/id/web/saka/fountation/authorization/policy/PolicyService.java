package id.web.saka.fountation.authorization.policy;

import id.web.saka.fountation.authorization.company.role.permission.CompanyRolePermissionService;
import id.web.saka.fountation.authorization.role.Role;
import id.web.saka.fountation.authorization.user.UserService;
import id.web.saka.fountation.authorization.user.role.UserRoleService;
import id.web.saka.fountation.common.messaging.outbox.OutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Service
public class PolicyService {

    private static final Logger log = LoggerFactory.getLogger(PolicyService.class);

    private final UserService userService;

    private final CompanyRolePermissionService rolePermissionService;

    private final UserRoleService userRoleService;

    private final OutboxService outboxService;

    public PolicyService(UserService userService, UserRoleService userRoleService, CompanyRolePermissionService rolePermissionService, OutboxService outboxService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.rolePermissionService = rolePermissionService;
        this.outboxService = outboxService;
    }


    public Mono<PolicyResponseDTO> evaluate(Jwt jwt, Long userId, Long companyId, PolicyRequestDTO request) {
        log.info("[POLICY] Evaluate | START | userId={} companyId={} request={}", userId, companyId, request);

        Mono<Long> userIdDecision = userId != null
                ? Mono.just(userId)
                : userService.getUserIdByEmail(jwt.getClaimAsString("https://example.com/email"))
                .switchIfEmpty(Mono.error(new RuntimeException("User not found in system")));

        return userIdDecision
                .flatMap(uid -> userRoleService.getRoleByUserIdandCompanyId(uid, companyId)
                        .flatMap(userRole -> {
                            log.debug("[POLICY] Evaluate | CHECKING_ROLE | uid={} role={} roleId={}", uid, userRole.getName(), userRole.getId());

                            // 1. SUPER_ADMIN BYPASS
                            if (userRole.getName().equals(Role.RoleName.SUPER_ADMIN)) {
                                log.info("[POLICY] Evaluate | BYPASS | uid={} role=SUPER_ADMIN | result=ALLOWED", uid);
                                PolicyResponseDTO response = new PolicyResponseDTO(true, "Allowed: Super Admin Privilege");
                                return outboxService.writeOutbox("POLICY_EVALUATION", "UID-" + uid, "ACCESS_ALLOWED_BYPASS", request)
                                        .thenReturn(response);
                            }

                            // 2. STANDARD PERMISSION CHECK
                            return rolePermissionService.getPermissionsByCompanyIdRoleId(companyId, userRole.getId())
                                    .filter(p -> request.action().equalsIgnoreCase(p.action()) &&
                                            request.resource().toLowerCase().startsWith(p.resource().toLowerCase()))
                                    .next()
                                    .map(p -> {
                                        log.info("[POLICY] Evaluate | SUCCESS | uid={} role={} resource={} action={} | result=ALLOWED", 
                                                uid, userRole.getName(), request.resource(), request.action());
                                        return new PolicyResponseDTO(true, "Allowed by role " + userRole.getName());
                                    })
                                    .defaultIfEmpty(new PolicyResponseDTO(false, "Denied: No matching permission"))
                                    .flatMap(response -> {
                                        String eventType = response.isAllow() ? "ACCESS_ALLOWED" : "ACCESS_DENIED";
                                        return outboxService.writeOutbox("POLICY_EVALUATION", "UID-" + uid, eventType, request)
                                                .thenReturn(response);
                                    });
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            log.warn("[POLICY] Evaluate | DENIED | uid={} companyId={} | reason=NO_ROLE_ASSIGNED", uid, companyId);
                            PolicyResponseDTO response = new PolicyResponseDTO(false, "Denied: No assigned role");
                            return outboxService.writeOutbox("POLICY_EVALUATION", "UID-" + uid, "ACCESS_DENIED_NO_ROLE", request)
                                    .thenReturn(response);
                        }))
                )
                .onErrorResume(e -> {
                    log.error("[POLICY] Evaluate | ERROR | msg={}", e.getMessage());
                    return Mono.just(new PolicyResponseDTO(false, "Denied: Internal error"));
                })
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(TimeoutException.class, e -> {
                    log.error("[POLICY] Evaluate | TIMEOUT | userId={} companyId={}", userId, companyId);
                    return Mono.just(new PolicyResponseDTO(false, "Denied: Timeout"));
                });
    }






}
