package id.web.saka.fountation.authorization.user.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class UserRoleController {

    private static final Logger log = LoggerFactory.getLogger(UserRoleController.class);

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/authorization/user/role/update/companyId/{companyId}/userId/{userId}")
    public Mono<UserRole> updateUserRole(@RequestBody Mono<UserRole> payload,
                                         @PathVariable Long companyId,
                                         @PathVariable Long userId) {
        log.info("[API] UserRole | Update | START | companyId={} userId={}", companyId, userId);

        return payload
                .flatMap(payloadDTO ->
                        userRoleService.updateUserRoles(companyId, payloadDTO)
                )
                .doOnSuccess(v -> log.info("[API] UserRole | Update | SUCCESS"))
                .doOnError(error -> log.error("[API] UserRole | Update | ERROR | msg={}", error.getMessage()));
    }

    @PostMapping("/authorization/user/role/add/companyId/{companyId}/userId/{userId}")
    public Mono<UserRole> addUserRole(@RequestBody Mono<UserRole> payload,
                                      @PathVariable Long companyId,
                                      @PathVariable Long userId) {
        log.info("[API] UserRole | Add | START | companyId={} userId={}", companyId, userId);

        return payload
                .flatMap(payloadDTO ->
                        userRoleService.addUserRole(companyId, userId, payloadDTO)
                )
                .doOnSuccess(v -> log.info("[API] UserRole | Add | SUCCESS"))
                .doOnError(error -> log.error("[API] UserRole | Add | ERROR | msg={}", error.getMessage()));
    }

    @GetMapping("/authorization/user/role/detail/companyId/{companyId}/userId/{userId}")
    public Mono<UserRole> getRoleByUserIdAndCompanyId(@PathVariable Long companyId,
                                                      @PathVariable Long userId) {
        log.info("[API] UserRole | Detail | START | companyId={} userId={}", companyId, userId);

        return userRoleService.getByUserIdAndCompanyId(userId, companyId);
    }


}
