package id.web.saka.fountation.authorization.user.role;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface UserRoleGrpcMapper {

    // --- UserRoleDTO <-> UserRoleProto ---

    UserRoleDTO toDto(UserRoleProto proto);
    UserRoleProto entityToProto(UserRole entity);
    UserRoleProto toProto(UserRoleDTO dto);
    UserRole protoToEntity(UserRoleProto proto);
    UserRole toEntity(UserRoleDTO dto);

    // --- Converters ---

    @Named("timestampToZonedDateTime")
    default ZonedDateTime timestampToZonedDateTime(Timestamp timestamp) {
        if (timestamp == null || (timestamp.getSeconds() == 0 && timestamp.getNanos() == 0)) return null;
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()), ZoneId.systemDefault());
    }

    @Named("zonedDateTimeToTimestamp")
    default Timestamp zonedDateTimeToTimestamp(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return Timestamp.getDefaultInstance();
        return Timestamp.newBuilder()
                .setSeconds(zonedDateTime.toEpochSecond())
                .setNanos(zonedDateTime.getNano())
                .build();
    }
}
