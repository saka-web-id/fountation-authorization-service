package id.web.saka.fountation.authorization.permission;

import id.web.saka.fountation.authorization.role.permission.RolePermission;
import id.web.saka.fountation.authorization.role.permission.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

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

    public Flux<PermissionDTO> findAll() {
        return permissionRepository.findAll()
                .map(permissionMapper::toDTO);
    }


}
