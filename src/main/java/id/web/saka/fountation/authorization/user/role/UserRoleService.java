package id.web.saka.fountation.authorization.user.role;

import id.web.saka.fountation.authorization.company.CompanyRole;
import id.web.saka.fountation.authorization.role.Role;
import id.web.saka.fountation.authorization.role.RoleService;
import id.web.saka.fountation.authorization.user.UserDTO;
import id.web.saka.fountation.common.messaging.outbox.OutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserRoleService {

    private static final Logger log = LoggerFactory.getLogger(UserRoleService.class);
    private final UserRoleRepository userRoleRepository;

    private final RoleService roleService;

    private final OutboxService outboxService;

    public UserRoleService(UserRoleRepository userRoleRepository, RoleService roleService, OutboxService outboxService) {
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
        this.outboxService = outboxService;
    }

    public Mono<Role> getRoleByUserIdandCompanyId(Long userId, Long companyId) {
        return userRoleRepository.findByUserIdAndCompanyId(userId, companyId)
                .flatMap(userRole -> roleService.getRoleById(userRole.getRoleId()))
                .doOnNext(role -> {
                    if (role == null) {
                        throw new RuntimeException("Role not found for userId: " + userId );
                    } else {
                        log.debug("[USER_ROLE] Fetch | SUCCESS | uid={} role={}", userId, role.getName());
                    }
                });

    }

    public Mono<UserRole> getByUserIdAndCompanyId(Long userId, Long companyId) {
        return userRoleRepository.findByUserIdAndCompanyId(userId, companyId);
    }

    public Mono<UserRole> updateUserRoles(Long companyId, UserRole payloadDTO) {
        log.info("[USER_ROLE] Update | START | companyId={} userId={}", companyId, payloadDTO.getUserId());
        return userRoleRepository.save(payloadDTO)
                .flatMap(saved -> outboxService.writeOutbox("USER_ROLE", "UR-" + saved.getId(), "USER_ROLE_UPDATED", saved)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("[USER_ROLE] Update | SUCCESS | id={}", saved.getId()));
    }

    public Mono<UserRole> addUserRole(Long companyId, Long userId, UserRole payloadDTO) {
        log.info("[USER_ROLE] Add | START | companyId={} userId={}", companyId, userId);
        return userRoleRepository.save(payloadDTO)
                .flatMap(saved -> outboxService.writeOutbox("USER_ROLE", "UR-" + saved.getId(), "USER_ROLE_ADDED", saved)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("[USER_ROLE] Add | SUCCESS | id={}", saved.getId()));
    }

    public Mono<UserRole> assignRoleToUser(UserDTO user, CompanyRole companyRole) {
        log.info("[USER_ROLE] Assign | START | userId={} roleId={}", user.getId(), companyRole.getRoleId());
        return userRoleRepository.save(new UserRole(user.getId(), companyRole.getCompanyId(), companyRole.getRoleId()))
                .flatMap(saved -> outboxService.writeOutbox("USER_ROLE", "UR-" + saved.getId(), "USER_ROLE_ASSIGNED", saved)
                        .thenReturn(saved))
                .doOnSuccess(saved -> log.info("[USER_ROLE] Assign | SUCCESS | id={}", saved.getId()));
    }

    public Flux<UserRole> getUserRoleByRoleName(Role.RoleName roleName) {
        return roleService.getRoleByName(roleName).flatMap(role -> {
            log.debug("[USER_ROLE] FetchByRoleName | roleName={}", roleName);
            return userRoleRepository.findAllByRoleId(role.getId())
                    .distinct(UserRole::getUserId);
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
