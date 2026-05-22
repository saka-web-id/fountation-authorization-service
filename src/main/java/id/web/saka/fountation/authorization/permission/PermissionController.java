package id.web.saka.fountation.authorization.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @GetMapping("/authorization/permission/list/companyId/{companyId}/userId/{userId}")
    public Flux<PermissionDTO> getAllPermissions(@PathVariable Long companyId, @PathVariable Long userId ) {
        log.info("[API] Permission | List | START | companyId={} userId={}", companyId, userId);

        return permissionService.findAll();
    }

    @GetMapping("/authorization/permission/detail/companyId/{companyId}/userId/{userId}/valueId/{id}")
    public Mono<PermissionDTO> getPermissionById(@PathVariable Long id) {
        log.info("[API] Permission | Detail | START | id={}", id);
        return permissionService.getPermissionById(id);
    }

    @PostMapping("/authorization/permission/add/companyId/{companyId}/userId/{userId}")
    public Mono<PermissionDTO> addPermission(@RequestBody PermissionDTO dto) {
        log.info("[API] Permission | Add | START | resource={} action={}", dto.resource(), dto.action());
        return permissionService.save(dto);
    }

    @PutMapping("/authorization/permission/update/companyId/{companyId}/userId/{userId}/valueId/{id}")
    public Mono<PermissionDTO> updatePermission(@PathVariable Long id, @RequestBody PermissionDTO dto) {
        log.info("[API] Permission | Update | START | id={}", id);
        return permissionService.update(id, dto);
    }

    @GetMapping("/authorization/permission/total/companyId/{companyId}/userId/{userId}")
    public Mono<Integer> countAllPermissionByCompanyId(@PathVariable Long companyId, @PathVariable Long userId ) {
        log.info("[API] Permission | Total | START | companyId={} userId={}", companyId, userId);

        return permissionService.countAllPermissionByCompanyId(companyId, userId);
    }


}
