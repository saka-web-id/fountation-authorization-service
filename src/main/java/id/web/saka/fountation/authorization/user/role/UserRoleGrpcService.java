package id.web.saka.fountation.authorization.user.role;

import id.web.saka.fountation.authorization.user.registration.UserRegistrationMapper;
import id.web.saka.fountation.authorization.user.registration.UserRegistrationService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class UserRoleGrpcService extends UserRoleServiceGrpc.UserRoleServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(UserRoleGrpcService.class);

    private final UserRoleService userRoleService;
    private final UserRegistrationService userRegistrationService;
    private final UserRoleGrpcMapper mapper;
    private final UserRegistrationMapper userRegistrationMapper;

    public UserRoleGrpcService(UserRoleService userRoleService,
                               UserRegistrationService userRegistrationService,
                               UserRoleGrpcMapper mapper,
                               UserRegistrationMapper userRegistrationMapper) {
        log.info("[GRPC] UserRole | INIT");

        this.userRoleService = userRoleService;
        this.userRegistrationService = userRegistrationService;
        this.mapper = mapper;
        this.userRegistrationMapper = userRegistrationMapper;
    }

    @Override
    public void getRoleByUserIdAndCompanyId(UserRoleRequest request, StreamObserver<UserRoleProto> responseObserver) {
        log.info("[GRPC] UserRole | GetRole | START | userId={} companyId={}", request.getUserId(), request.getCompanyId());

        userRoleService.getByUserIdAndCompanyId(request.getUserId(), request.getCompanyId())
                .map(mapper::entityToProto)
                .subscribe(
                        response -> {
                            log.info("[GRPC] UserRole | GetRole | SUCCESS");
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError("GetRole", error, responseObserver),
                        () -> {
                            log.info("[GRPC] UserRole | GetRole | EMPTY");
                            responseObserver.onNext(UserRoleProto.getDefaultInstance());
                            responseObserver.onCompleted();
                        }
                );
    }

    @Override
    public void updateUserRoles(UpdateUserRolesRequest request,
                                StreamObserver<UserRoleProto> responseObserver) {
        log.info("[GRPC] UserRole | Update | START | companyId={} userId={}",
                request.getCompanyId(), request.getUserId());

        userRoleService.updateUserRoles(
                        request.getCompanyId(),
                        mapper.protoToEntity(request.getUserRole())
                )
                .map(mapper::entityToProto)
                .subscribe(
                        response -> {
                            log.info("[GRPC] UserRole | Update | SUCCESS");
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError("Update", error, responseObserver)
                );
    }

    @Override
    public void addUserRole(AddUserRoleRequest request,
                            StreamObserver<UserRoleProto> responseObserver) {
        log.info("[GRPC] UserRole | Add | START | companyId={} userId={}",
                request.getCompanyId(), request.getUserId());
        userRoleService.addUserRole(
                        request.getCompanyId(),
                        request.getUserId(),
                        mapper.protoToEntity(request.getUserRole())
                )
                .map(mapper::entityToProto)
                .subscribe(
                        response -> {
                            log.info("[GRPC] UserRole | Add | SUCCESS");
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError("Add", error, responseObserver)
                );
    }

    @Override
    public void assignRoleToNewUser(UserRegistrationProto request,
                                    StreamObserver<UserRegistrationProto> responseObserver) {
        log.info("[GRPC] UserRole | AssignToNewUser | START | email={}", request.getUser().getEmail());
        userRegistrationService.assignRoleToNewUser(userRegistrationMapper.toDto(request))
                .map(userRegistrationMapper::toProto)
                .subscribe(
                        response -> {
                            log.info("[GRPC] UserRole | AssignToNewUser | SUCCESS");
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError("AssignToNewUser", error, responseObserver)
                );
    }

    private void handleGrpcError(String action, Throwable t, StreamObserver<?> responseObserver) {
        log.error("[GRPC] UserRole | {} | ERROR | msg={}", action, t.getMessage());
        responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription(t.getMessage())
                .asRuntimeException());
    }
}