package id.web.saka.fountation.authorization.permission;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PermissionDTO (
        @JsonProperty("permissionId") Long id,
        @JsonProperty("permissionName") String name,
        @JsonProperty("isSuperAdmin") boolean superAdmin,
        @JsonProperty("permissionResource") String resource,
        @JsonProperty("permissionAction") String action,
        @JsonProperty("permissionDescription") String description,
        @JsonProperty("isAssigned") boolean isAssigned
) {

}
