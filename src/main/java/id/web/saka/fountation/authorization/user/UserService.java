package id.web.saka.fountation.authorization.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final WebClient webClientUser;

    public UserService(@Qualifier("webClientUser") WebClient webClientConfig) {
        this.webClientUser = webClientConfig;
    }


    public Mono<Long> getUserIdByEmail(String emailUser) {
        return webClientUser.get()
                .uri("/api/v0/user/detail/getIdByEmail/" + emailUser)
                .retrieve()
                .bodyToMono(Long.class);
    }
}
