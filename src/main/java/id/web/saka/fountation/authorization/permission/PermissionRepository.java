package id.web.saka.fountation.authorization.permission;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends ReactiveCrudRepository<Permission, Long> {


}
