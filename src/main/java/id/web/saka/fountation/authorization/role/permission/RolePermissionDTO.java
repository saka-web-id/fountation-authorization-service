package id.web.saka.fountation.authorization.role.permission;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.web.saka.fountation.authorization.permission.PermissionDTO;
import id.web.saka.fountation.authorization.role.Role;

import java.util.List;

public class RolePermissionDTO {

    public RolePermissionDTO() {

    }

    public RolePermissionDTO(Role role, List<PermissionDTO> permissions) {
        this.roleId = role.getId();
        this.roleName = role.getName().name();
        this.roleDescription = role.getDescription();
        this.permissions = permissions;
    }

    @JsonProperty("roleId")
    private Long roleId;

    @JsonProperty("roleName")
    private String roleName;

    @JsonProperty("roleDescription")
    private String roleDescription;

    @JsonProperty("permissions")
    private List<PermissionDTO> permissions;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    public List<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionDTO> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "RolePermissionDTO{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleDescription='" + roleDescription + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
