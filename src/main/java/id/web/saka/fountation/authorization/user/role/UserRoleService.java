package id.web.saka.fountation.authorization.user.role;

import id.web.saka.fountation.authorization.company.CompanyRole;
import id.web.saka.fountation.authorization.role.Role;
import id.web.saka.fountation.authorization.role.RoleMapper;
import id.web.saka.fountation.authorization.role.RoleService;
import id.web.saka.fountation.authorization.user.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserRoleService {

    Logger log = LoggerFactory.getLogger(UserRoleService.class);
    private final UserRoleRepository userRoleRepository;

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    public UserRoleService(UserRoleRepository userRoleRepository, RoleService roleService, RoleMapper roleMapper) {
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    public Mono<Role> getRoleByUserIdandCompanyId(Long userId, Long companyId) {
        return userRoleRepository.findByUserIdAndCompanyId(userId, companyId)
                .flatMap(userRole -> roleService.getRoleById(userRole.getRoleId()))
                .doOnNext(role -> {
                    if (role == null) {
                        throw new RuntimeException("Role not found for userId: " + userId );
                    } else {
                        log.info("Found role: " + role.toString() + " for userId: " + userId );
                    }
                });

    }

    public Mono<UserRole> getByUserIdAndCompanyId(Long userId, Long companyId) {
        return userRoleRepository.findByUserIdAndCompanyId(userId, companyId);
    }

    public Mono<UserRole> updateUserRoles(Long companyId, UserRole payloadDTO) {

        return userRoleRepository.save(payloadDTO)
                .doOnNext(savedUserRole -> log.info("Updated UserRole: {}", savedUserRole));
    }

    public Mono<UserRole> addUserRole(Long companyId, Long userId, UserRole payloadDTO) {
        return userRoleRepository.save(payloadDTO)
                .doOnNext(savedUserRole -> log.info("Added UserRole: {}", savedUserRole));
    }

    public Mono<UserRole> assignRoleToUser(UserDTO user, CompanyRole companyRole) {
        return userRoleRepository.save(new UserRole(user.getId(), companyRole.getCompanyId(), companyRole.getRoleId()))
                .doOnNext(savedUserRole -> log.info("Assigned UserRole: {}", savedUserRole));
    }

    public Flux<UserRole> getUserRoleByRoleName(Role.RoleName roleName) {

        return roleService.getRoleByName(roleName).flatMap(role -> {
            return userRoleRepository.findAllByRoleId(role.getId())
                    .doOnNext(userRole -> log.info("Found UserRole: {}", userRole))
                    .distinct(UserRole::getUserId); // distinct by userId

        });
    }

    /*public Mono<Role> getRoleByUserId(Long userId) {

        return userRoleRepository.findByUserId(userId)
                .flatMap(userRole -> roleService.getRoleById(userRole.getRoleId()))
                .doOnNext(role -> {
                    if (role == null) {
                        throw new RuntimeException("Role not found for userId: " + userId );
                    } else {
                        log.info("Found role: " + role.toString() + " for userId: " + userId );
                    }
                });

    }*/

}
