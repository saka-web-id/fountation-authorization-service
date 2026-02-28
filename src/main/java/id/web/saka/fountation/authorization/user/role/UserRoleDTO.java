package id.web.saka.fountation.authorization.user.role;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserRoleDTO(
        @JsonProperty("id") Long id,

        @JsonProperty("userId") Long userId,

        @JsonProperty("roleId") Long roleId,

        @JsonProperty("companyId") Long companyId
) {
}
