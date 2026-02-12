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

    Logger log = LoggerFactory.getLogger(UserRegistrationController.class);

    private final UserRegistrationService userRegistrationService;


    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/authorization/user/registration")
    public Mono<UserRegistrationDTO> assignRoleToNewUser(@RequestBody Mono<UserRegistrationDTO> payload) {
        log.info("Registering UserRole for new user: {}", payload);

        return payload.
                doOnNext(dto -> log.info("Incoming assignRoleToNewUser payload: {}", dto))
                .flatMap(userRegistrationService::assignRoleToNewUser
                ).doOnError(error -> log.error("Error assigning role to new user: " + error.getMessage()));
    }

}
