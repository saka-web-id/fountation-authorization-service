package id.web.saka.fountation.authorization.organization.company;

import com.google.protobuf.Timestamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    // --- CompanyDTO <-> CompanyProto ---

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "timestampToZonedDateTime")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "timestampToZonedDateTime")
    CompanyDTO toDto(id.web.saka.fountation.organization.company.CompanyProto proto);

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "zonedDateTimeToTimestamp")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "zonedDateTimeToTimestamp")
    id.web.saka.fountation.organization.company.CompanyProto toProto(CompanyDTO dto);

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
