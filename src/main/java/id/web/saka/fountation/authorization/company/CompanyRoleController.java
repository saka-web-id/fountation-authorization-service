package id.web.saka.fountation.authorization.company;

import id.web.saka.fountation.authorization.role.RoleDTO;
import id.web.saka.fountation.authorization.role.permission.RolePermissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v0")
public class CompanyRoleController {

    Logger logger = LoggerFactory.getLogger(CompanyRoleController.class);
    private final CompanyRoleService companyRoleService;

    public CompanyRoleController(CompanyRoleService companyRoleService) {
        this.companyRoleService = companyRoleService;
    }

    @GetMapping("/authorization/company/role/list/{companyId}")
    public Flux<RoleDTO> getAllRolesByCompanyId(@PathVariable Long companyId) {
        logger.info("Fetching all roles for companyId: " + companyId);
        return companyRoleService.getAllRolesByCompanyId(companyId);
    }

}
