package id.web.saka.fountation.authorization.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class PermissionController {

    Logger log = LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    @GetMapping("/authorization/permission/list/companyId/{companyId}/userId/{userId}")
    public Flux<PermissionDTO> getAllPermissions(@PathVariable Long companyId, @PathVariable Long userId ) {
        log.info("getAllPermissions: companyId={}, userId={}", companyId, userId);

        return permissionService.findAll();
    }

    @GetMapping("/authorization/permission/total/companyId/{companyId}/userId/{userId}")
    public Mono<Integer> countAllPermissionByCompanyId(@PathVariable Long companyId, @PathVariable Long userId ) {
        log.info("countAllPermissionByCompanyId: companyId={}, userId={}", companyId, userId);

        return permissionService.countAllPermissionByCompanyId(companyId, userId);
    }


}
