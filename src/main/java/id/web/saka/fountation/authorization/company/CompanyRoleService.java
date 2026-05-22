package id.web.saka.fountation.authorization.company;

import id.web.saka.fountation.authorization.organization.company.CompanyDTO;
import id.web.saka.fountation.authorization.role.Role;
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

    private static final Logger log = LoggerFactory.getLogger(CompanyRoleService.class);

    private final CompanyRoleRepository companyRoleRepository;

    private final RoleService roleService;

    private final RoleMapper roleMapper;

    public CompanyRoleService(CompanyRoleRepository companyRoleRepository, RoleService roleService, RoleMapper roleMapper) {
        this.companyRoleRepository = companyRoleRepository;
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    public Flux<RoleDTO> getAllRolesByCompanyId(Long companyId) {
        log.info("[COMPANY_ROLE] FetchAll | START | companyId={}", companyId);

        return findAllByCompanyId(companyId).flatMap(companyRole ->
                roleService.getRoleById(companyRole.getRoleId())
                        .map(roleMapper::toDTO));

    }

    private Flux<CompanyRole> findAllByCompanyId(Long companyId) {
        return companyRoleRepository.findAllByCompanyId(companyId);
    }

    public Mono<CompanyRole> saveCompanyRole(Long companyId, RoleDTO savedRoled) {
        log.info("[COMPANY_ROLE] Save | START | companyId={} roleId={}", companyId, savedRoled.getId());
        return companyRoleRepository.save(new CompanyRole(companyId, savedRoled.getId()));
    }

    public Mono<CompanyRole> createDefaultRolesForNewCompany(CompanyDTO company) {
        log.info("[COMPANY_ROLE] CreateDefault | START | companyId={}", company.id());
        return Flux.fromArray(Role.RoleName.values())
                .filter(role -> role != Role.RoleName.SUPER_ADMIN)
                .flatMap(role -> roleService.getRoleByName(role)
                        .flatMap(roleEntity -> {
                            CompanyRole companyRole = new CompanyRole(company.id(), roleEntity.getId());
                            return companyRoleRepository.save(companyRole)
                                    .doOnNext(saved -> log.info("[COMPANY_ROLE] CreateDefault | SAVED | role={} roleId={}", role, saved.getRoleId()))
                                    .filter(saved -> role == Role.RoleName.ADMIN);
                        })
                )
                .next();
    }
}
