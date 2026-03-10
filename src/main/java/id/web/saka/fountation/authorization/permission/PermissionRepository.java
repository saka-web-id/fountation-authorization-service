package id.web.saka.fountation.authorization.permission;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PermissionRepository extends ReactiveCrudRepository<Permission, Long> {

    Mono<Permission> findByResourceAndAction(String resource, String action);

}
