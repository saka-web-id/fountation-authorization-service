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
        log.info("Starting role assignment for user: {} in company: {}",
                userRegistrationDTO.user(), userRegistrationDTO.company());

        return companyRoleService.createDefaultRolesForNewCompany(userRegistrationDTO.company())
                .doOnNext(role -> log.info("Step 1: Default roles created. RoleID: {}", role.getRoleId()))
                .flatMap(companyRole -> {
                    log.info("Step 2: Assigning ADMIN role {} to user ID: {}",
                            companyRole.getRoleId(), userRegistrationDTO.user().getId());

                    return userRoleService.assignRoleToUser(userRegistrationDTO.user(), companyRole)
                            .doOnNext(assigned -> log.info("Step 2 Success: Role assigned to user."))
                            .flatMap(assignedUserRole -> {
                                log.info("Step 3: Setting up permissions for company: {} and role: {}",
                                        userRegistrationDTO.company().id(), companyRole.getRoleId());

                                return companyRolePermissionService.setupPermissionsForRole(
                                                userRegistrationDTO.company().id(), companyRole.getRoleId())
                                        .doOnSuccess(v -> log.info("Step 3 Success: Permissions configured."))
                                        .thenReturn(assignedUserRole);
                            })
                            .then(Mono.defer(() -> {
                                log.info("Step 4: Attempting SUPER_ADMIN assignment...");
                                return assignSuperAdminRoleToNewCompanyAdmin(userRegistrationDTO, companyRole);
                            }))
                            .doOnSuccess(v -> log.info("Step 4 Success: Super Admin logic completed."))
                            .thenReturn(userRegistrationDTO);
                })
                .doOnError(e -> log.error("CRITICAL FAILURE in assignRoleToNewUser: {} - Message: {}",
                        e.getClass().getSimpleName(), e.getMessage()))
                .doOnTerminate(() -> log.info("Role assignment flow terminated."));
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
