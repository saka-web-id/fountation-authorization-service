package id.web.saka.fountation.authorization.user.role;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table(value = "user_role")
public class UserRole {

    @Id
    @Column("id")
    private Long id;


    @Column("user_id")
    private Long userId;

    @Column("company_id")
    private Long companyId;

    @Column("role_id")
    private Long roleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
