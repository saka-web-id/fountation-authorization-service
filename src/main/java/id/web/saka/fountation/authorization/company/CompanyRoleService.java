package id.web.saka.fountation.authorization.company;

import id.web.saka.fountation.authorization.role.RoleDTO;
import id.web.saka.fountation.authorization.role.RoleMapper;
import id.web.saka.fountation.authorization.role.RoleService;
import id.web.saka.fountation.authorization.role.permission.RolePermissionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CompanyRoleService {

    Logger logger = LoggerFactory.getLogger(CompanyRoleService.class);

    private final CompanyRoleRepository companyRoleRepository;

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    public CompanyRoleService(CompanyRoleRepository companyRoleRepository, RoleService roleService, RoleMapper roleMapper) {
        this.companyRoleRepository = companyRoleRepository;
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    public Flux<RoleDTO> getAllRolesByCompanyId(Long companyId) {
        logger.info("Starting retrieval of roles for companyId: {}", companyId);

        return findAllByCompanyId(companyId).flatMap(companyRole ->
                roleService.getRoleById(companyRole.getRoleId())
                        .map(roleMapper::toDTO));

    }

    private Flux<CompanyRole> findAllByCompanyId(Long companyId) {
        logger.info("Querying CompanyRoleRepository for companyId: {}", companyId);

        return companyRoleRepository.findAllByCompanyId(companyId);
    }

    public Mono<CompanyRole> saveCompanyRole(Long companyId, RoleDTO savedRoled) {

        return companyRoleRepository.save(new CompanyRole(companyId, savedRoled.getId()));
    }
}
