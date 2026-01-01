package id.web.saka.fountation.authorization.company;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(value = "company_role")
public class CompanyRole {

    public CompanyRole(Long companyId, Long roleId) {
        this.companyId = companyId;
        this.roleId = roleId;
    }

    @Id
    @Column("id")
    private Long id;

    @Column("company_id")
    private Long companyId;

    @Column("role_id")
    private Long roleId;

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

    @Override
    public String toString() {
        return "CompanyRole{" +
                "id=" + id +
                ", companyId=" + companyId +
                ", roleId=" + roleId +
                '}';
    }
}
