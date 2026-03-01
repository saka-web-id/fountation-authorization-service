package id.web.saka.fountation.authorization.policy;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@GrpcService
public class PolicyGrpcService extends PolicyServiceGrpc.PolicyServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(PolicyGrpcService.class);

    private final PolicyService policyService;
    private final PolicyGrpcMapper mapper;

    public PolicyGrpcService(PolicyService policyService, PolicyGrpcMapper mapper) {
        this.policyService = policyService;
        this.mapper = mapper;
    }

    @Override
    public void checkPolicy(PolicyRequest request, StreamObserver<PolicyResponse> responseObserver) {
        log.info("Received gRPC policy check request: companyId={}, userId={}", 
                request.getCompanyId(), request.hasUserId() ? request.getUserId() : "N/A");

        PolicyRequestDTO dto = mapper.toDTO(request);
        Long userId = request.hasUserId() ? request.getUserId() : null;
        Long companyId = request.getCompanyId();

        // Pass null for Jwt as gRPC call is service-to-service
        policyService.evaluate(null, userId, companyId, dto)
                .map(mapper::toProto)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> {
                            log.error("Error evaluating policy via gRPC", error);
                            responseObserver.onError(io.grpc.Status.INTERNAL
                                    .withDescription("Internal error during policy evaluation: " + error.getMessage())
                                    .asRuntimeException());
                        }
                );
    }
}
