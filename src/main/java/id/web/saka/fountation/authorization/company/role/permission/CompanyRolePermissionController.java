package id.web.saka.fountation.authorization.company.role.permission;

import id.web.saka.fountation.authorization.role.permission.RolePermissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class CompanyRolePermissionController {

    Logger log = LoggerFactory.getLogger(CompanyRolePermissionController.class);

    private final CompanyRolePermissionService rolePermissionService;

    public CompanyRolePermissionController(CompanyRolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping("/authorization/company/role/permission/detail/companyId/{companyId}/userId/{userId}/valueCompanyId/{valueCompanyId}/valueRoleId/{roleId}")
    public Mono<CompanyRolePermissionDTO> getCompanyRolePermissionsByCompanyRoleId(@PathVariable Long companyId, @PathVariable Long userId, @PathVariable Long valueCompanyId, @PathVariable Long roleId) {
        return rolePermissionService.getCompanyRolePermissionsByCompanyRoleId(valueCompanyId, roleId);
    }

    @GetMapping("/authorization/company/role/permission/detail/companyId/{companyId}/userId/{userId}/valueCompanyId/{valueCompanyId}/valueUserId/{valueUserId}")
    public Mono<CompanyRolePermissionDTO> getRoleByUserId(@PathVariable Long valueCompanyId, @PathVariable("userId") Long valueUserId) {

        log.info("Fetching RolePermissionDTO for userId: " + valueUserId);
        return rolePermissionService.getCompanyRolePermissionsByCompanyRoleId(valueCompanyId, valueUserId);
    }

    @PostMapping("/authorization/company/role/permission/update/companyId/{companyId}/userId/{userId}/valueCompanyId/{valueCompanyId}/valueRoleId/{roleId}")
    public Mono<ResponseEntity<CompanyRolePermissionDTO>> updateRolePermissions(@PathVariable Long companyId, @PathVariable Long userId, @PathVariable Long valueCompanyId, @PathVariable Long roleId, @RequestBody Mono<CompanyRolePermissionDTO> payload) {
        log.info("updateRolePermissions|roleId:{}|START", roleId);

        return payload.flatMap(rolePermissionDTO -> rolePermissionService.updateRolePermissions(valueCompanyId, roleId, rolePermissionDTO)).map(ResponseEntity::ok)
                .doOnError(e -> log.error("Failed to update role permissions for roleId {}", roleId, e))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /*@PostMapping("/authorization/company/role/permission/add/companyId/{companyId}/userId/{userId}}/valueCompanyId/{valueCompanyId}")
    public Mono<ResponseEntity<RolePermissionDTO>> saveRolePermissions(@PathVariable Long companyId, @PathVariable Long userId, @PathVariable Long valueCompanyId, @RequestBody Mono<CompanyRolePermissionDTO> payload) {
        log.info("saveRolePermissions|companyId:{}|START", valueCompanyId);

        return payload.flatMap(rolePermissionDTO -> {
                    rolePermissionDTO.setRoleId(null); //need to set null since want to add new record
                    return rolePermissionService.addRolePermissions(valueCompanyId, rolePermissionDTO);
                })
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Failed to update role permissions for companyId:{}", valueCompanyId, e))
                .onErrorResume(e -> {
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }*/

}
