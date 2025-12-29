package id.web.saka.fountation.authorization.permission;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toEntity(PermissionDTO dto);

    PermissionDTO toDTO(Permission entity);
}
