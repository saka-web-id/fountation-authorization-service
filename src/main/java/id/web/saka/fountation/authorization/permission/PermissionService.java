package id.web.saka.fountation.authorization.permission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

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

    public Mono<PermissionDTO> save(PermissionDTO dto) {
        log.info("[PERMISSION] Save | START | dto={}", dto);
        return permissionRepository.findByResourceAndAction(dto.resource(), dto.action())
                .flatMap(existing -> Mono.<Permission>error(new RuntimeException("Permission with this resource and action already exists.")))
                .switchIfEmpty(Mono.defer(() -> permissionRepository.save(permissionMapper.toEntity(dto))))
                .map(permissionMapper::toDTO)
                .doOnSuccess(saved -> log.info("[PERMISSION] Save | SUCCESS | id={}", saved.id()))
                .doOnError(e -> log.error("[PERMISSION] Save | ERROR | msg={}", e.getMessage()));
    }

    public Mono<PermissionDTO> update(Long id, PermissionDTO dto) {
        log.info("[PERMISSION] Update | START | id={} dto={}", id, dto);

        return permissionRepository.findById(id)
                .flatMap(existing -> {
                    return permissionRepository.findByResourceAndAction(dto.resource(), dto.action())
                            .filter(p -> !p.getId().equals(id))
                            .flatMap(p -> Mono.<Permission>error(new RuntimeException("Permission with this resource and action already exists.")))
                            .switchIfEmpty(Mono.defer(() -> {
                                Permission entity = permissionMapper.toEntity(dto);
                                entity.setId(id);

                                log.info("[PERMISSION] Update | SAVING | id={} entity={}", id, entity);
                                return permissionRepository.save(entity);
                            }));
                })
                .map(permissionMapper::toDTO)
                .doOnSuccess(updated -> log.info("[PERMISSION] Update | SUCCESS | id={}", updated.id()))
                .doOnError(e -> log.error("[PERMISSION] Update | ERROR | id={} msg={}", id, e.getMessage()));
    }

    public Mono<Integer> countAllPermissionByCompanyId(Long companyId, Long userId) {

        return permissionRepository.count().map(Long::intValue);
    }
}
