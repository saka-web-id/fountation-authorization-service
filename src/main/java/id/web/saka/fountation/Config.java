package id.web.saka.fountation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class Config {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user1 = User.withUsername("user2")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .authorities("profile", "openid")
                .build();

        UserDetails admin = User.withUsername("testuser")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .authorities("profile", "openid", "message.read", "message.write")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }

    // Define the password encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /*@Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http.authorizeExchange(authorizeRequests -> authorizeRequests.anyExchange()
                        .authenticated())
                .formLogin(withDefaults());
        return http.build();
    }*/

    /*@Bean
    public RegisteredClientRepository registeredClientRepository() {

        // Define the "gateway-client"
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("fountation-authorization-service-id") // This is the ID used in the Gateway's application.yml

                // The secret must be encoded. The {noop} prefix is for plaintext in development only.
                // For production use, you would hash this with passwordEncoder.encode("secret123")
                //.clientSecret(passwordEncoder.encode("gateway-client-secret")) // This is the SECRET
                .clientSecret("{noop}fountation-authorization-service-secret") // This is the SECRET

                .clientName("API Gateway Client")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE) // Standard OIDC flow
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) // For service-to-service calls
                .redirectUri("lb://fountation-gateway-service") // Matches the Gateway's expected redirect
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("api.read") // Custom scope your gateway might request
                .scope("api.write")
                .build();

        // Stores the client in memory (use JdbcRegisteredClientRepository for production)
        return new InMemoryRegisteredClientRepository(registeredClient);
    }*/
}
