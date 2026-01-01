package id.web.saka.fountation.authorization.company;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CompanyRoleRepository extends ReactiveCrudRepository<CompanyRole, Long> {
    Flux<CompanyRole> findAllByCompanyId(Long companyId);
}
