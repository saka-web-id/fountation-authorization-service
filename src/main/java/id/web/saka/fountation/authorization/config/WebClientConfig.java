package id.web.saka.fountation.authorization.config;

import id.web.saka.fountation.configbase.fountation.FountationProperties;
import id.web.saka.fountation.configbase.spring.security.SpringSecurityProperties;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
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

    private final FountationProperties fountationProperties;

    private final SpringSecurityProperties springSecurityProperties;

    public WebClientConfig(FountationProperties fountationProperties, SpringSecurityProperties springSecurityProperties) {
        this.fountationProperties = fountationProperties;
        this.springSecurityProperties = springSecurityProperties;
    }

    @Bean
    public HttpClient httpClient() {
        ConnectionProvider provider = ConnectionProvider.builder("fountation-authorization-pool")
                .maxIdleTime(Duration.ofSeconds(60)) // Increased from 20s
                .maxLifeTime(Duration.ofMinutes(5))  // Increased from 1m to match Gateway
                .evictInBackground(Duration.ofSeconds(30))
                .build();

        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000) // 30s connection timeout
                .responseTimeout(Duration.ofMinutes(2)); // Increased from 10s to 120s
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
                .baseUrl(fountationProperties.getService().getUsers().getUrl()) //fountation.service.users.url
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
                .uri(springSecurityProperties.getOauth2().getClient().getProvider().get("auth0").getTokenUri())//spring.security.oauth2.client.provider.auth0.token-uri
                .bodyValue(Map.of(
                        "client_id", springSecurityProperties.getOauth2().getClient().getRegistration().get("internal-service").getClientId(), //spring.security.oauth2.client.registration.internal-service.client-id
                        "client_secret", springSecurityProperties.getOauth2().getClient().getRegistration().get("internal-service").getClientSecret(), //spring.security.oauth2.client.registration.internal-service.client-secret
                        "audience", fountationProperties.getService().getSecurity().getJwt().getAudience(), //fountation.service.security.jwt.audience
                        "grant_type", springSecurityProperties.getOauth2().getClient().getRegistration().get("internal-service").getAuthorizationGrantType(), //spring.security.oauth2.client.registration.internal-service.authorization-grant-type
                        "scope", springSecurityProperties.getOauth2().getClient().getRegistration().get("internal-service").getScope() //spring.security.oauth2.client.registration.internal-service.scope
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .cache(Duration.ofMinutes(50));
    }
}
