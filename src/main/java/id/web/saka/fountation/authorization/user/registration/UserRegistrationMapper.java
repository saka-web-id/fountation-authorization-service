package id.web.saka.fountation.authorization.user.registration;

import com.google.protobuf.Timestamp;
import id.web.saka.fountation.authorization.user.role.UserRegistrationProto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface UserRegistrationMapper {

    // --- UserRegistrationDTO <-> UserRegistrationProto ---

    @Mapping(target = "user", source = "user")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "company", source = "company")
    @Mapping(target = "department", source = "department")
    @Mapping(target = "user.lastLoginAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "user.createdAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "user.updatedAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "account.createdAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "account.membershipStartDate", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "account.membershipEndDate", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "company.createdAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "company.updatedAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "department.createdAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "department.updatedAt", qualifiedByName = "timestampToZonedDateTime")
    UserRegistrationDTO toDto(UserRegistrationProto proto);

    @Mapping(target = "user", source = "user")
    @Mapping(target = "account", source = "account")
    @Mapping(target = "company", source = "company")
    @Mapping(target = "department", source = "department")
    @Mapping(target = "user.lastLoginAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "user.createdAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "user.updatedAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "account.createdAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "account.membershipStartDate", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "account.membershipEndDate", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "company.createdAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "company.updatedAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "department.createdAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "department.updatedAt", qualifiedByName = "zonedDateTimeToTimestamp")
    UserRegistrationProto toProto(UserRegistrationDTO dto);


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
