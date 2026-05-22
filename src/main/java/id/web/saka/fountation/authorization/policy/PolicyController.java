package id.web.saka.fountation.authorization.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/v0")
public class PolicyController {

    private static final Logger log = LoggerFactory.getLogger(PolicyController.class);

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/authorization/policy/check/companyId/{companyId}")
    public Mono<ResponseEntity<PolicyResponseDTO>> authorize(@AuthenticationPrincipal Jwt jwt, @RequestBody PolicyRequestDTO request, @PathVariable Long companyId) {
        log.info("[API] Policy | Authorize | START | companyId={}", companyId);


        return policyService.evaluate(jwt, null, companyId, request)
                .map(decision -> {
                    if (decision.isAllow()) {
                        log.info("[API] Policy | Authorize | ALLOWED | companyId={}", companyId);
                        return ResponseEntity.ok(decision);
                    } else {
                        log.warn("[API] Policy | Authorize | DENIED | companyId={} reason={}", companyId, decision.reason());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(decision);
                    }
                });
    }


    @PostMapping("/authorization/policy/check/companyId/{companyId}/userId/{userId}")
    public Mono<ResponseEntity<PolicyResponseDTO>> authorize(@AuthenticationPrincipal Jwt jwt, @RequestBody PolicyRequestDTO request, @PathVariable Long companyId, @PathVariable Long userId) {
        log.info("[API] Policy | Authorize | START | companyId={} userId={}", companyId, userId);

        return policyService.evaluate(jwt, userId, companyId, request)
                .map(decision -> {
                    if (decision.isAllow()) {
                        log.info("[API] Policy | Authorize | ALLOWED | companyId={} userId={}", companyId, userId);
                        return ResponseEntity.ok(decision);
                    } else {
                        log.warn("[API] Policy | Authorize | DENIED | companyId={} userId={} reason={}", companyId, userId, decision.reason());
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(decision);
                    }
                });
    }

}
