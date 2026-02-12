package id.web.saka.fountation.authorization.user.registration;

import id.web.saka.fountation.authorization.company.CompanyRole;
import id.web.saka.fountation.authorization.company.CompanyRoleService;
import id.web.saka.fountation.authorization.company.role.permission.CompanyRolePermissionService;
import id.web.saka.fountation.authorization.role.Role;
import id.web.saka.fountation.authorization.user.UserDTO;
import id.web.saka.fountation.authorization.user.role.UserRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserRegistrationService {

    Logger log = LoggerFactory.getLogger(UserRegistrationService.class);

    private final CompanyRoleService companyRoleService;

    private final UserRoleService userRoleService;

    private final CompanyRolePermissionService companyRolePermissionService;

    public UserRegistrationService(CompanyRoleService companyRoleService, UserRoleService userRoleService, CompanyRolePermissionService companyRolePermissionService) {
        this.companyRoleService = companyRoleService;
        this.userRoleService = userRoleService;
        this.companyRolePermissionService = companyRolePermissionService;
    }



    public Mono<UserRegistrationDTO> assignRoleToNewUser(UserRegistrationDTO userRegistrationDTO) {
        return companyRoleService.createDefaultRolesForNewCompany(userRegistrationDTO.company())
                .flatMap(companyRole -> {
                    // First: assign ADMIN role to the new user
                    log.info("Assigning role {} to new user {}", companyRole.getRoleId(), userRegistrationDTO.user());
                    return userRoleService.assignRoleToUser(userRegistrationDTO.user(), companyRole)
                            .flatMap(assignedUserRole -> {
                                // Second: set up role permissions
                                return companyRolePermissionService.setupPermissionsForRole(userRegistrationDTO.company().id(), companyRole.getRoleId())
                                        .thenReturn(assignedUserRole);
                            })
                            // Third: also assign SUPER_ADMIN role logic
                            .then(assignSuperAdminRoleToNewCompanyAdmin(userRegistrationDTO, companyRole))
                            // Finally: return the original DTO
                            .thenReturn(userRegistrationDTO);
                });
    }

    public Mono<Void> assignSuperAdminRoleToNewCompanyAdmin (
            UserRegistrationDTO userRegistrationDTO,
            CompanyRole companyRole) {

        return userRoleService.getUserRoleByRoleName(Role.RoleName.SUPER_ADMIN) // Flux<UserRole>
                .flatMap(userRole -> {
                    UserDTO newAdminUser = userRegistrationDTO.user();
                    newAdminUser.setId(userRole.getUserId());
                    CompanyRole newCompanyRole = new CompanyRole(companyRole.getCompanyId(), userRole.getRoleId());
                    return userRoleService.assignRoleToUser(newAdminUser, newCompanyRole); // Mono<Void> or Mono<UserRole>
                })
                .then(); // collapse Flux into Mono<Void>
    }

}
