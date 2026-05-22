package id.web.saka.fountation.authorization.company.role.permission;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService; // Import dari net.devh
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class CompanyRolePermissionGrpcService extends CompanyRolePermissionServiceGrpc.CompanyRolePermissionServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(CompanyRolePermissionGrpcService.class);
    private final CompanyRolePermissionService service;

    public CompanyRolePermissionGrpcService(CompanyRolePermissionService service) {
        this.service = service;
    }

    @Override
    public void getCompanyRolePermission(GetCompanyRolePermissionRequest request,
                                         StreamObserver<CompanyRolePermissionProto> responseObserver) {
        log.info("[GRPC] CompanyRolePermission | Fetch | START | companyId={} userId={}",
                request.getCompanyId(), request.getUserId());

        service.getCompanyRolePermissionsByCompanyRoleId(request.getCompanyId(), request.getUserId())
                .map(this::mapToProto)
                .doOnSuccess(proto -> {
                    log.info("[GRPC] CompanyRolePermission | Fetch | SUCCESS");
                    responseObserver.onNext(proto);
                    responseObserver.onCompleted();
                })
                .doOnError(e -> {
                    log.error("[GRPC] CompanyRolePermission | Fetch | ERROR | msg={}", e.getMessage());
                    responseObserver.onError(io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException());
                })
                .subscribe(); 
    }

    private CompanyRolePermissionProto mapToProto(CompanyRolePermissionDTO dto) {
        return CompanyRolePermissionProto.newBuilder()
                .setRoleId(dto.roleId())
                .setCompanyId(dto.companyId())
                .setRoleName(dto.roleName())
                .setRoleDescription(dto.roleDescription())
                .build();
    }
}