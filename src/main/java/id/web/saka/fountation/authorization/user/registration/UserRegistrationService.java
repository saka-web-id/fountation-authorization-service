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

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationService.class);

    private final CompanyRoleService companyRoleService;

    private final UserRoleService userRoleService;

    private final CompanyRolePermissionService companyRolePermissionService;

    public UserRegistrationService(CompanyRoleService companyRoleService, UserRoleService userRoleService, CompanyRolePermissionService companyRolePermissionService) {
        this.companyRoleService = companyRoleService;
        this.userRoleService = userRoleService;
        this.companyRolePermissionService = companyRolePermissionService;
    }



    public Mono<UserRegistrationDTO> assignRoleToNewUser(UserRegistrationDTO userRegistrationDTO) {
        log.info("[USER_REGISTRATION] AssignRole | START | user={} company={}",
                userRegistrationDTO.user().getEmail(), userRegistrationDTO.company().id());

        return companyRoleService.createDefaultRolesForNewCompany(userRegistrationDTO.company())
                .doOnNext(role -> log.info("[USER_REGISTRATION] AssignRole | DEFAULT_ROLES_CREATED | roleId={}", role.getRoleId()))
                .flatMap(companyRole -> {
                    log.info("[USER_REGISTRATION] AssignRole | ASSIGNING_ADMIN | roleId={} userId={}",
                            companyRole.getRoleId(), userRegistrationDTO.user().getId());

                    return userRoleService.assignRoleToUser(userRegistrationDTO.user(), companyRole)
                            .doOnNext(assigned -> log.info("[USER_REGISTRATION] AssignRole | ADMIN_ASSIGNED"))
                            .flatMap(assignedUserRole -> {
                                log.info("[USER_REGISTRATION] AssignRole | SETUP_PERMISSIONS | companyId={} roleId={}",
                                        userRegistrationDTO.company().id(), companyRole.getRoleId());

                                return companyRolePermissionService.setupPermissionsForRole(
                                                userRegistrationDTO.company().id(), companyRole.getRoleId())
                                        .doOnSuccess(v -> log.info("[USER_REGISTRATION] AssignRole | PERMISSIONS_SETUP_SUCCESS"))
                                        .thenReturn(assignedUserRole);
                            })
                            .then(Mono.defer(() -> {
                                log.info("[USER_REGISTRATION] AssignRole | ATTEMPT_SUPER_ADMIN | userId={}", userRegistrationDTO.user().getId());
                                return assignSuperAdminRoleToNewCompanyAdmin(userRegistrationDTO, companyRole);
                            }))
                            .doOnSuccess(v -> log.info("[USER_REGISTRATION] AssignRole | SUPER_ADMIN_COMPLETED"))
                            .thenReturn(userRegistrationDTO);
                })
                .doOnError(e -> log.error("[USER_REGISTRATION] AssignRole | ERROR | type={} msg={}",
                        e.getClass().getSimpleName(), e.getMessage()))
                .doOnTerminate(() -> log.info("[USER_REGISTRATION] AssignRole | END"));
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
