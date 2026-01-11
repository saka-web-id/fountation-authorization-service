package id.web.saka.fountation.authorization.policy;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PolicyResponseDTO(
        @JsonProperty("policyIsAllow") boolean isAllow,
        @JsonProperty("policyReason")  String reason
) {
}
