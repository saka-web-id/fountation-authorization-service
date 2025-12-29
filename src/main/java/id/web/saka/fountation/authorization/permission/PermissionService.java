package id.web.saka.fountation.authorization.permission;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    public Mono<PermissionDTO> getPermissionById(Long permissionId) {
        return permissionRepository.findById(permissionId)
                .map(permissionMapper::toDTO);
    }
}
