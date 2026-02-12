package id.web.saka.fountation.authorization.util.filter;

import id.web.saka.fountation.authorization.policy.PolicyRequestDTO;
import id.web.saka.fountation.authorization.policy.PolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AuthorizationFilter implements WebFilter {

    Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    private final PolicyService policyService;

    // Define excluded paths (prefixes or exact matches)
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/v0/authorization/policy/check",
            "/api/v0/authorization/user/registration"
    );


    public AuthorizationFilter(PolicyService policyService) {
        this.policyService = policyService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        logger.info("filter|START|path={}", path);

        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            logger.info("filter|SKIP authorization for path={}", path);
            return chain.filter(exchange);
        }

        // Use regex to extract IDs instead of manual loop
        Long companyId;
        Long userId;

        Matcher matcher = Pattern.compile("/companyId/(\\d+)/userId/(\\d+)").matcher(path);
        if (matcher.find()) {
            companyId = Long.valueOf(matcher.group(1));
            userId = Long.valueOf(matcher.group(2));
            logger.info("Parsed companyId={}, userId={}", companyId, userId);
        } else {
            userId = 0L;
            companyId = 0L;
        }

        return exchange.getPrincipal()
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .flatMap(jwt -> {
                    String method = exchange.getRequest().getMethod().name();
                    PolicyRequestDTO authRequest = new PolicyRequestDTO("", method, path);

                    return policyService.evaluate(jwt, userId, companyId, authRequest)
                            .flatMap(decision -> {
                                if (decision.isAllow()) {
                                    return chain.filter(exchange);
                                } else {
                                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                                    return exchange.getResponse().setComplete();
                                }
                            });
                });
    }
}

