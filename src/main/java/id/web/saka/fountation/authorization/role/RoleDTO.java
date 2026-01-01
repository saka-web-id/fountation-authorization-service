package id.web.saka.fountation.authorization.role;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class RoleDTO {

    @JsonProperty("roleId")
    private Long id;

    @JsonProperty("roleName")
    private Role.RoleName name;

    @JsonProperty("roleDescription")
    private String description;

    @JsonProperty("roleCreatedAt")
    private ZonedDateTime createdAt;

    @JsonProperty("roleUpdatedAt")
    private ZonedDateTime updatedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role.RoleName getName() {
        return name;
    }

    public void setName(Role.RoleName name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "RoleDTO{" +
                "id=" + id +
                ", name=" + name +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
