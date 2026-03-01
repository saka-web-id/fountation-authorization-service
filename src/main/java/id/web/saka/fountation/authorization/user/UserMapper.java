package id.web.saka.fountation.authorization.user;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // --- UserDTO <-> UserProto ---

    @Mapping(target = "lastLoginAt", source = "lastLoginAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "verified", source = "isVerified")
    UserDTO toDto(id.web.saka.fountation.user.UserProto proto);

    @Mapping(target = "lastLoginAt", source = "lastLoginAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "isVerified", source = "verified")
    id.web.saka.fountation.user.UserProto toProto(UserDTO dto);

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
