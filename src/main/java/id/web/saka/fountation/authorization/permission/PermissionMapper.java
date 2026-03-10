package id.web.saka.fountation.authorization.permission;

import id.web.saka.fountation.permission.PermissionProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "superAdmin", source = "superAdmin")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "description", source = "description")
    Permission toEntity(PermissionDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "superAdmin", source = "superAdmin")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "description", source = "description")
    PermissionDTO toDTO(Permission entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "description", source = "description")
    PermissionDTO toDTO(PermissionProto proto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "resource", source = "resource")
    @Mapping(target = "action", source = "action")
    @Mapping(target = "description", source = "description")
    PermissionProto toProto(PermissionDTO dto);

    default boolean map(Boolean value) {
        return value != null && value;
    }
}
