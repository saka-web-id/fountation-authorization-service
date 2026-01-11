package id.web.saka.fountation.authorization.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Env {

    @Value("${fountation.service.users.url}")
    private String fountationServiceUserUrl;

    @Value("${spring.security.oauth2.client.registration.internal-service.client-id}")
    private String clientRegistrationInternalServiceClientId;

    @Value("${spring.security.oauth2.client.registration.internal-service.client-secret}")
    private String clientRegistrationInternalServiceClientSecret;

    @Value("${spring.security.oauth2.client.registration.internal-service.authorization-grant-type}")
    private String clientRegistrationInternalServiceGrantType;

    @Value("${spring.security.oauth2.client.registration.internal-service.scope}")
    private String clientRegistrationInternalServiceScope;

    @Value("${spring.security.oauth2.client.provider.auth0.token-uri}")
    private String clientRegistrationInternalServiceTokenUri;


    public String getFountationServiceUserUrl() {
        return fountationServiceUserUrl;
    }

    public void setFountationServiceUserUrl(String fountationServiceUserUrl) {
        this.fountationServiceUserUrl = fountationServiceUserUrl;
    }

    public String getClientRegistrationInternalServiceClientId() {
        return clientRegistrationInternalServiceClientId;
    }

    public void setClientRegistrationInternalServiceClientId(String clientRegistrationInternalServiceClientId) {
        this.clientRegistrationInternalServiceClientId = clientRegistrationInternalServiceClientId;
    }

    public String getClientRegistrationInternalServiceClientSecret() {
        return clientRegistrationInternalServiceClientSecret;
    }

    public void setClientRegistrationInternalServiceClientSecret(String clientRegistrationInternalServiceClientSecret) {
        this.clientRegistrationInternalServiceClientSecret = clientRegistrationInternalServiceClientSecret;
    }

    public String getClientRegistrationInternalServiceGrantType() {
        return clientRegistrationInternalServiceGrantType;
    }

    public void setClientRegistrationInternalServiceGrantType(String clientRegistrationInternalServiceGrantType) {
        this.clientRegistrationInternalServiceGrantType = clientRegistrationInternalServiceGrantType;
    }

    public String getClientRegistrationInternalServiceScope() {
        return clientRegistrationInternalServiceScope;
    }

    public void setClientRegistrationInternalServiceScope(String clientRegistrationInternalServiceScope) {
        this.clientRegistrationInternalServiceScope = clientRegistrationInternalServiceScope;
    }

    public String getClientRegistrationInternalServiceTokenUri() {
        return clientRegistrationInternalServiceTokenUri;
    }

    public void setClientRegistrationInternalServiceTokenUri(String clientRegistrationInternalServiceTokenUri) {
        this.clientRegistrationInternalServiceTokenUri = clientRegistrationInternalServiceTokenUri;
    }
}
