package id.web.saka.fountation.authorization.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRoleRepository extends ReactiveCrudRepository<UserRole, Long> {

    Mono<UserRole> findByCompanyIdAndUserId(Long companyId, Long userId);

    Flux<UserRole> findAllByCompanyId(Long companyId);

}
