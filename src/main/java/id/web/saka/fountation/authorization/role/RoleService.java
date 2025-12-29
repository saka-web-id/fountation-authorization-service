package id.web.saka.fountation.authorization.role;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Mono<Role> getRoleById(String roleId) {
        return roleRepository.findById(Long.parseLong(roleId));
    }
}
