package id.web.saka.fountation.authorization.role;

import id.web.saka.fountation.authorization.user.UserRoleService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public Mono<Role> getRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    public Mono<RoleDTO> saveRole(Role roleDTOMono) {
        return roleRepository.save(roleDTOMono)
                .map(roleMapper::toDTO);
    }
}
