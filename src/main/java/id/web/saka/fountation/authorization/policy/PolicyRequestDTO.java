package id.web.saka.fountation.authorization.policy;

import com.fasterxml.jackson.annotation.JsonProperty;
public record PolicyRequestDTO(
        @JsonProperty("policySubject")  String subject,
        @JsonProperty("policyAction") String action,
        @JsonProperty("policyResource") String resource) {
}
