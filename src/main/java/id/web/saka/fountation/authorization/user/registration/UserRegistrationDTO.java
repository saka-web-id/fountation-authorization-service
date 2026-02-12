package id.web.saka.fountation.authorization.user.registration;

import com.fasterxml.jackson.annotation.JsonProperty;
import id.web.saka.fountation.authorization.account.AccountDTO;
import id.web.saka.fountation.authorization.organization.company.CompanyDTO;
import id.web.saka.fountation.authorization.organization.department.DepartmentDTO;
import id.web.saka.fountation.authorization.user.UserDTO;

public record UserRegistrationDTO(
        @JsonProperty("user") UserDTO user,
        @JsonProperty("account") AccountDTO account,
        @JsonProperty("company") CompanyDTO company,
        @JsonProperty("department") DepartmentDTO department
) {
}
