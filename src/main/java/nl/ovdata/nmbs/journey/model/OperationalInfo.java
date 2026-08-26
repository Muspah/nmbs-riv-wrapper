package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OperationalInfo(
        @JsonProperty("Name") String name,
        @JsonProperty("Num") String number,
        @JsonProperty("Operator") String operator,
        @JsonProperty("FromExtId") String fromExtId,
        @JsonProperty("ToExtId") String toExtId
) {
}
