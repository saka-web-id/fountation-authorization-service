package id.web.saka.fountation.authorization.organization.department;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record DepartmentDTO (
        @JsonProperty("departmentId") Long id,

        @JsonProperty("companyId") Long companyId,

        @JsonProperty("departmentName") String name,

        @JsonProperty("departmentStatus") String status,

        @JsonProperty("departmentDescription") String description,

        @JsonProperty("departmentCreatedAt") ZonedDateTime createdAt,

        @JsonProperty("departmentUpdatedAt") ZonedDateTime updatedAt
) {
}
