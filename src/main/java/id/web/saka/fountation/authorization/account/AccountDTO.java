package id.web.saka.fountation.authorization.account;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record AccountDTO(
        @JsonProperty("accountNumber")
        String accountNumber,

        @JsonProperty("accountStatus")
        String accountStatus,

        @JsonProperty("membershipType")
        String membershipType,

        @JsonProperty("membershipStatus")
        String membershipStatus,

        @JsonProperty("createdAt")
        ZonedDateTime createdAt,

        @JsonProperty("membershipStartDate")
        ZonedDateTime membershipStartDate,

        @JsonProperty("membershipEndDate")
        ZonedDateTime membershipEndDate
) {

}
