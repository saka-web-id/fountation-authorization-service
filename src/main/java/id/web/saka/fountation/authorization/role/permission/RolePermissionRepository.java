package id.web.saka.fountation.authorization.role.permission;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RolePermissionRepository extends ReactiveCrudRepository<RolePermission, Long> {

    Flux<RolePermission> findAllByRoleId(Long roleIds);

    Flux<Object> deleteAllByRoleId(Long roleId);
}
