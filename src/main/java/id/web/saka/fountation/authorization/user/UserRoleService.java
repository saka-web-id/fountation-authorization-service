package id.web.saka.fountation.authorization.user;

import id.web.saka.fountation.authorization.role.Role;
import id.web.saka.fountation.authorization.role.RoleService;
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

    public UserRoleService(UserRoleRepository userRoleRepository, RoleService roleService) {
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
    }

    public Mono<Role> getRoleByCompanyIdAndUserId(Long companyId, Long userId) {

        return userRoleRepository.findByCompanyIdAndUserId(companyId, userId)
                .flatMap(userRole -> roleService.getRoleById(String.valueOf(userRole.getRoleId())))
                .doOnNext(role -> {
                    if (role == null) {
                        throw new RuntimeException("Role not found for userId: " + userId + " in companyId: " + companyId);
                    } else {
                        log.info("Found role: " + role.toString() + " for userId: " + userId + " in companyId: " + companyId);
                    }
                });

    }

    public Flux<Role> getRolesByCompanyId(Long companyId) {

        return userRoleRepository.findAllByCompanyId(companyId)
                .flatMap(userRole -> roleService.getRoleById(String.valueOf(userRole.getRoleId())));

    }
}
