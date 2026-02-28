package id.web.saka.fountation.authorization.account;

import com.google.protobuf.Timestamp;
import id.web.saka.fountation.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    // --- AccountDTO <-> AccountProto ---

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "membershipStartDate", source = "membershipStartDate", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "membershipEndDate", source = "membershipEndDate", qualifiedByName = "timestampToZonedDateTime")
    AccountDTO toDto(Account.AccountProto proto);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "membershipStartDate", source = "membershipStartDate", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "membershipEndDate", source = "membershipEndDate", qualifiedByName = "zonedDateTimeToTimestamp")
    Account.AccountProto toProto(AccountDTO dto);

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
