package id.web.saka.fountation.authorization.company.role.permission;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "company_role_permission")
public class CompanyRolePermission {

    @Id
    @Column("id")
    private Long id;

    @Column("company_id")
    private Long companyId;

    @Column("role_id")
    private Long roleId;

    @Column("permission_id")
    private Long permissionId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    @Override
    public String toString() {
        return "CompanyRolePermission{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", roleId=" + roleId +
                ", permissionId=" + permissionId +
                '}';
    }
}
