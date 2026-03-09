package id.web.saka.fountation.authorization.config;

import id.web.saka.fountation.authorization.util.Env;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServerBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.Map;

@Configuration
public class WebClientConfig {

    private final Env env;

    public WebClientConfig(Env env) {
        this.env = env;
    }

    @Bean
    public HttpClient httpClient() {
        ConnectionProvider provider = ConnectionProvider.builder("fountation-authorization-pool")
                .maxIdleTime(Duration.ofSeconds(20)) // Clear connections that have been idle for 20s
                .maxLifeTime(Duration.ofMinutes(1))  // Max life of a connection
                .evictInBackground(Duration.ofSeconds(30)) // Evict idle connections in background
                .build();

        return HttpClient.create(provider)
                .responseTimeout(Duration.ofSeconds(10)); // Request timeout
    }

    @Bean
    public WebClient webClientUser(HttpClient httpClient) {
        // 1. Buat Cache untuk Token agar tidak membebani server Auth
        Mono<String> tokenCache = getAccessToken(httpClient)
                .cache(
                        token -> Duration.ofMinutes(50), // Cache jika sukses (50 menit)
                        error -> Duration.ZERO,           // Jangan cache jika error (coba lagi langsung)
                        () -> Duration.ZERO               // Jangan cache jika kosong
                );

        // 2. Buat Filter yang menggunakan Cache tersebut
        ExchangeFilterFunction authFilter = (request, next) ->
                tokenCache.flatMap(token -> {
                    ClientRequest filteredRequest = ClientRequest.from(request)
                            .headers(headers -> headers.setBearerAuth(token))
                            .build();
                    return next.exchange(filteredRequest);
                });

        // 3. Bangun WebClient
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(env.getFountationServiceUserUrl())
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .filter(authFilter)
                .build();
    }

    private Mono<String> getAccessToken(HttpClient httpClient) {
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        return webClient.post()
                .uri(env.getClientRegistrationInternalServiceTokenUri())
                .bodyValue(Map.of(
                        "client_id", env.getClientRegistrationInternalServiceClientId(),
                        "client_secret", env.getClientRegistrationInternalServiceClientSecret(),
                        "audience", env.getFountationServiceSecurityJwtAudience(), // YOUR_API_IDENTIFIER
                        "grant_type", env.getClientRegistrationInternalServiceGrantType(),
                        "scope", env.getClientRegistrationInternalServiceScope()
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .cache(Duration.ofMinutes(50));
    }
}
