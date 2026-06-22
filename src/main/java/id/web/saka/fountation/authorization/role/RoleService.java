package id.web.saka.fountation.authorization.role;

import id.web.saka.fountation.common.messaging.outbox.OutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    private final OutboxService outboxService;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper, OutboxService outboxService) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.outboxService = outboxService;
    }

    public Mono<Role> getRoleById(Long roleId) {
        return roleRepository.findById(roleId);
    }

    public Mono<RoleDTO> saveRole(Role roleDTOMono) {
        log.info("[ROLE] Save | START | roleName={}", roleDTOMono.getName());
        return roleRepository.save(roleDTOMono)
                .map(roleMapper::toDTO)
                .flatMap(saved -> outboxService.writeOutbox("ROLE", "ROLE-" + saved.getId(), "ROLE_SAVED", saved)
                        .thenReturn(saved));
    }

    public Flux<Role> getRoleByName(Role.RoleName role) {
        return roleRepository.findByName(role);
    }
}
