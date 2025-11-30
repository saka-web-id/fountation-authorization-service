package id.web.saka.fountation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import reactor.core.publisher.Mono;

import java.net.URL;

@Controller
public class AuthorizationController {

    @GetMapping("/")
    public Mono<String> index() {
        return  Mono.just("login");
    }

    @GetMapping("/login")
    public Mono<String> login(Model model) {

        model.addAttribute("pageTitle", "Home Page");

        return Mono.just("login");
    }


}
