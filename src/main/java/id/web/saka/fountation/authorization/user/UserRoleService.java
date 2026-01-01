package id.web.saka.fountation.authorization.user;

import id.web.saka.fountation.authorization.role.Role;
import id.web.saka.fountation.authorization.role.RoleMapper;
import id.web.saka.fountation.authorization.role.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

    public Mono<Role> getRoleByUserId(Long userId) {

        return userRoleRepository.findByUserId(userId)
                .flatMap(userRole -> roleService.getRoleById(userRole.getRoleId()))
                .doOnNext(role -> {
                    if (role == null) {
                        throw new RuntimeException("Role not found for userId: " + userId );
                    } else {
                        log.info("Found role: " + role.toString() + " for userId: " + userId );
                    }
                });

    }

}
