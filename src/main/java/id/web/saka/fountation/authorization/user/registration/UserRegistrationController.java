package id.web.saka.fountation.authorization.user.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v0")
public class UserRegistrationController {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationController.class);

    private final UserRegistrationService userRegistrationService;


    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/authorization/user/registration")
    public Mono<UserRegistrationDTO> assignRoleToNewUser(@RequestBody Mono<UserRegistrationDTO> payload) {
        return payload
                .doOnNext(dto -> log.info("[API] UserRegistration | Register | START | email={}", dto.user().getEmail()))
                .flatMap(userRegistrationService::assignRoleToNewUser)
                .doOnSuccess(dto -> log.info("[API] UserRegistration | Register | SUCCESS"))
                .doOnError(error -> log.error("[API] UserRegistration | Register | ERROR | msg={}", error.getMessage()));
    }

}
