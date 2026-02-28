package id.web.saka.fountation.authorization.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class UserDTO {

    @JsonProperty("userId")
    private Long id;

    @JsonProperty("userEmail")
    private String email;

    @JsonProperty("userName")
    private String name;

    @JsonProperty("userPhone")
    private String phone;

    @JsonProperty("userStatus")
    private String status;

    @JsonProperty("userIsVerified")
    private boolean isVerified;

    @JsonProperty("userLastLoginAt")
    private ZonedDateTime lastLoginAt;

    @JsonProperty("userCreatedAt")
    private ZonedDateTime createdAt;

    @JsonProperty("userUpdatedAt")
    private ZonedDateTime updateAt;

    @JsonProperty("userLeaderId")
    private Long leaderId;

    @JsonProperty("userNote")
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public ZonedDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(ZonedDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(ZonedDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                ", isVerified=" + isVerified +
                ", lastLoginAt=" + lastLoginAt +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                ", leaderId=" + leaderId +
                ", note='" + note + '\'' +
                '}';
    }

}
