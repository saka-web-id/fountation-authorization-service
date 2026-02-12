package id.web.saka.fountation.authorization.organization.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record CompanyDTO (
        @JsonProperty("companyId") Long id,
        @JsonProperty("companyName") String name,
        @JsonProperty("companyAddress") String address,
        @JsonProperty("companyPhone") String phone,
        @JsonProperty("companyEmail") String email,
        @JsonProperty("companyWebsite") String website,
        @JsonProperty("companyDescription") String description,
        @JsonProperty("companyLogoUrl") String logoUrl,
        @JsonProperty("companyTaxId") String taxId,
        @JsonProperty("companyRegistrationId") String registrationNumber,
        @JsonProperty("companyStatus") String status,
        @JsonProperty("companyIndustry") String industry,
        @JsonProperty("companyType") String type,
        @JsonProperty("companyCreatedAt")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX") ZonedDateTime createdAt,
        @JsonProperty("companyUpdatedAt")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssXXX") ZonedDateTime updatedAt,
        @JsonProperty("companyIsDefault") boolean isDefault
) {
}
