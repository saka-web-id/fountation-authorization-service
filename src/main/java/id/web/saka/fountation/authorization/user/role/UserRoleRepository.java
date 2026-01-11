package id.web.saka.fountation.authorization.user.role;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRoleRepository extends ReactiveCrudRepository<UserRole, Long> {


    Mono<UserRole> findByUserIdAndCompanyId(Long userId, Long companyId);

}
