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
        log.info("Received gRPC request for company role permissions: companyId={}, userId={}",
                request.getCompanyId(), request.getUserId());

        service.getCompanyRolePermissionsByCompanyRoleId(request.getCompanyId(), request.getUserId())
                .map(this::mapToProto)
                .doOnSuccess(proto -> {
                    responseObserver.onNext(proto);
                    responseObserver.onCompleted();
                })
                .doOnError(e -> {
                    log.error("Error in gRPC service: ", e);
                    responseObserver.onError(io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException());
                })
                .subscribe(); // Wajib di-subscribe karena gRPC base class tidak reaktif murni
    }

    private CompanyRolePermissionProto mapToProto(CompanyRolePermissionDTO dto) {
        log.info("Mapping CompanyRolePermissionDTO to Proto: {}", dto);

        return CompanyRolePermissionProto.newBuilder()
                .setRoleId(dto.roleId())
                .setCompanyId(dto.companyId())
                .setRoleName(dto.roleName())
                .setRoleDescription(dto.roleDescription())
                .build();
    }
}