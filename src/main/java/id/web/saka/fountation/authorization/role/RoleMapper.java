package id.web.saka.fountation.authorization.role;

import id.web.saka.fountation.authorization.role.permission.RolePermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toEntity(RoleDTO dto);

    RoleDTO toDTO(Role entity);

    @Mapping(source = "roleId", target = "id")
    @Mapping(source = "roleName", target = "name")
    @Mapping(source = "roleDescription", target = "description")
    Role toRequestEntity(RolePermissionDTO dto);

    default ZonedDateTime toOffset(Instant instant) {
        return instant == null ? null :
                instant.atZone(ZoneOffset.UTC);
    }

    // OffsetDateTime (GMT+7) → Instant (UTC)
    default Instant toInstant(ZonedDateTime zdt) {
        return zdt == null ? null : zdt.toInstant();
    }

}
