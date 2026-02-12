package id.web.saka.fountation.authorization.role.type;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class RoleTypeService {

    Logger logger = org.slf4j.LoggerFactory.getLogger(RoleTypeService.class);

    private final RoleTypeRepository roleTypeRepository;

    public RoleTypeService(RoleTypeRepository roleTypeRepository) {
        this.roleTypeRepository = roleTypeRepository;
    }

}
