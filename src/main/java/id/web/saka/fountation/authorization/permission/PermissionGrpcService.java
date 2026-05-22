package id.web.saka.fountation.authorization.permission;

import id.web.saka.fountation.permission.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@GrpcService
public class PermissionGrpcService extends PermissionGrpcServiceGrpc.PermissionGrpcServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(PermissionGrpcService.class);
    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    public PermissionGrpcService(PermissionService permissionService, PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public void getPermissionList(PermissionListRequest request, StreamObserver<PermissionListResponse> responseObserver) {
        log.info("[GRPC] Permission | List | START | companyId={} userId={}",
                request.getCompanyId(), request.getUserId());

        permissionService.findAll()
                .map(permissionMapper::toProto)
                .collectList()
                .map(this::buildResponse)
                .doOnSuccess(response -> {
                    log.info("[GRPC] Permission | List | SUCCESS | count={}", response.getPermissionsCount());
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                })
                .doOnError(e -> {
                    log.error("[GRPC] Permission | List | ERROR | msg={}", e.getMessage());
                    responseObserver.onError(io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException());
                })
                .subscribe();
    }

    @Override
    public void getPermissionTotal(PermissionListRequest request, StreamObserver<PermissionTotalResponse> responseObserver) {
        log.info("[GRPC] Permission | Total | START | companyId={} userId={}",
                request.getCompanyId(), request.getUserId());

        permissionService.countAllPermissionByCompanyId(request.getCompanyId(), request.getUserId())
                .map(total -> PermissionTotalResponse.newBuilder().setTotal(total).build())
                .doOnSuccess(response -> {
                    log.info("[GRPC] Permission | Total | SUCCESS | total={}", response.getTotal());
                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                })
                .doOnError(e -> {
                    log.error("[GRPC] Permission | Total | ERROR | msg={}", e.getMessage());
                    responseObserver.onError(io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException());
                })
                .subscribe();
    }

    private PermissionListResponse buildResponse(List<PermissionProto> protos) {
        return PermissionListResponse.newBuilder()
                .addAllPermissions(protos)
                .build();
    }
}
