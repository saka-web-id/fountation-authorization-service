package id.web.saka.fountation.authorization.user.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class UserRoleController {

    Logger log = LoggerFactory.getLogger(UserRoleController.class);

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping("/authorization/user/role/update/companyId/{companyId}/userId/{userId}")
    public Mono<UserRole> updateUserRole(@RequestBody Mono<UserRole> payload,
                                         @PathVariable Long companyId,
                                         @PathVariable Long userId) {
        log.info("Updating UserRole for in companyId: " + companyId + " by userId: " + userId);

        return payload.
                doOnNext(dto -> log.info("Incoming updateUserRole payload: {}", dto))
                .flatMap(payloadDTO ->
                        userRoleService.updateUserRoles(companyId, payloadDTO)
                ).doOnError(error -> log.error("Error updating UserRole: " + error.getMessage()));
    }

    @PostMapping("/authorization/user/role/add/companyId/{companyId}/userId/{userId}")
    public Mono<UserRole> addUserRole(@RequestBody Mono<UserRole> payload,
                                      @PathVariable Long companyId,
                                      @PathVariable Long userId) {
        log.info("Adding UserRole in companyId: " + companyId + " by userId: " + userId);

        return payload.
                doOnNext(dto -> log.info("Incoming addUserRole payload: {}", dto))
                .flatMap(payloadDTO ->
                        userRoleService.addUserRole(companyId, userId, payloadDTO)
                ).doOnError(error -> log.error("Error adding UserRole: " + error.getMessage()));
    }

    @GetMapping("/authorization/user/role/detail/companyId/{companyId}/userId/{userId}")
    public Mono<UserRole> getRoleByUserIdAndCompanyId(@PathVariable Long companyId,
                                                      @PathVariable Long userId) {
        log.info("Fetching UserRole for companyId: {} and userId: {}", companyId, userId);

        return userRoleService.getByUserIdAndCompanyId(userId, companyId);
    }


}
