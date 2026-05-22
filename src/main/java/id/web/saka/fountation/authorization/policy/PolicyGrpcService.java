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
        log.info("[GRPC] Policy | Check | START | companyId={} userId={}",
                request.getCompanyId(), request.hasUserId() ? request.getUserId() : "N/A");

        PolicyRequestDTO dto = mapper.toDTO(request);
        Long userId = request.hasUserId() ? request.getUserId() : null;
        Long companyId = request.getCompanyId();

        policyService.evaluate(null, userId, companyId, dto)
                .map(mapper::toProto)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("[GRPC] Policy | Check | EMPTY | sending default denied");
                    return Mono.just(PolicyResponse.newBuilder()
                            .setIsAllow(false)
                            .setReason("Denied: Evaluation resulted in no data")
                            .build());
                }))
                .subscribe(
                        response -> {
                            log.info("[GRPC] Policy | Check | SUCCESS | allowed={}", response.getIsAllow());
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        },
                        error -> {
                            log.error("[GRPC] Policy | Check | ERROR | msg={}", error.getMessage());
                            io.grpc.Status status = (error instanceof java.util.concurrent.TimeoutException)
                                    ? io.grpc.Status.DEADLINE_EXCEEDED
                                    : io.grpc.Status.INTERNAL;

                            responseObserver.onError(status
                                    .withDescription("Policy error: " + error.getMessage())
                                    .asRuntimeException());
                        }
                );
    }
}
