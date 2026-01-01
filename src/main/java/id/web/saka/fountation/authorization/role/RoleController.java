package id.web.saka.fountation.authorization.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v0")
public class RoleController {

    Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    public RoleController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/authorization/role/detail/{roleId}")
    public Mono<RoleDTO> getRoleById(@PathVariable("roleId") Long roleId) {
        log.info("Fetching role for roleId: " + roleId);

        return roleService.getRoleById(roleId)
                .map(roleMapper::toDTO);
    }

}
