package id.web.saka.fountation.authorization.company.role.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.web.saka.fountation.authorization.permission.PermissionDTO;

import java.util.List;

public record CompanyRolePermissionDTO(
        @JsonProperty("roleId") Long roleId,

        @JsonProperty("companyId") Long companyId,

        @JsonProperty("roleName") String roleName,

        @JsonProperty("roleDescription") String roleDescription,

        @JsonProperty("permissions") List<PermissionDTO> permissions
) {
}
