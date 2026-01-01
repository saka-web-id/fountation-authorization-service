package id.web.saka.fountation.authorization.user;

import id.web.saka.fountation.authorization.role.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v0")
public class UserRoleController {

    Logger log = LoggerFactory.getLogger(UserRoleController.class);

}
