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
        log.info("Initializing UserRoleGrpcService with UserRoleService and UserRegistrationService");

        this.userRoleService = userRoleService;
        this.userRegistrationService = userRegistrationService;
        this.mapper = mapper;
        this.userRegistrationMapper = userRegistrationMapper;
    }

    @Override
    public void getRoleByUserIdAndCompanyId(UserRoleRequest request, StreamObserver<UserRoleProto> responseObserver) {
        log.info("Received gRPC request to get role by userId: {} and companyId: {}", request.getUserId(), request.getCompanyId());

        userRoleService.getByUserIdAndCompanyId(request.getUserId(), request.getCompanyId())
                .map(mapper::entityToProto)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError(error, responseObserver),
                        () -> {
                            // If empty, we can return an empty proto or error
                            responseObserver.onNext(UserRoleProto.getDefaultInstance());
                            responseObserver.onCompleted();
                        }
                );
    }

    @Override
    public void updateUserRoles(UpdateUserRolesRequest request,
                                StreamObserver<UserRoleProto> responseObserver) {
        log.info("Received gRPC request to update user roles: companyId={}, userId={}, userRole={}",
                request.getCompanyId(), request.getUserId(), request.getUserRole());

        userRoleService.updateUserRoles(
                        request.getCompanyId(),
                        mapper.protoToEntity(request.getUserRole())
                )
                .map(mapper::entityToProto)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError(error, responseObserver)
                );
    }

    @Override
    public void addUserRole(AddUserRoleRequest request,
                            StreamObserver<UserRoleProto> responseObserver) {
        userRoleService.addUserRole(
                        request.getCompanyId(),
                        request.getUserId(),
                        mapper.protoToEntity(request.getUserRole())
                )
                .map(mapper::entityToProto)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError(error, responseObserver)
                );
    }

    @Override
    public void assignRoleToNewUser(UserRegistrationProto request,
                                    StreamObserver<UserRegistrationProto> responseObserver) {
        userRegistrationService.assignRoleToNewUser(userRegistrationMapper.toDto(request))
                .map(userRegistrationMapper::toProto)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> handleGrpcError(error, responseObserver)
                );
    }

    private void handleGrpcError(Throwable t, StreamObserver<?> responseObserver) {
        log.error("gRPC Service Error: ", t);
        responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription(t.getMessage())
                .asRuntimeException());
    }
}