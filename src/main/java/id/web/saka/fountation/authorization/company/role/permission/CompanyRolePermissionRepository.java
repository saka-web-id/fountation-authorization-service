package id.web.saka.fountation.authorization.company.role.permission;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CompanyRolePermissionRepository extends ReactiveCrudRepository<CompanyRolePermission, Long> {

    /*Flux<CompanyRolePermission> findAllByRoleId(Long roleIds);*/

    Flux<Object> deleteAllByRoleId(Long roleId);

    Flux<CompanyRolePermission> findAllByCompanyIdAndRoleId(Long companyId, Long roleId);

    Flux<CompanyRolePermission> findAllByRoleId(Long roleId);
}
