package id.web.saka.fountation.authorization.util.converter;

import id.web.saka.fountation.authorization.role.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class RoleNameWriteConverter implements Converter<Role.RoleName, String> {

    @Override
    public String convert(Role.RoleName source) {
        return source.name();
    }
}
