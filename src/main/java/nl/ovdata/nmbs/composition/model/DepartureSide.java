package nl.ovdata.nmbs.composition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DepartureSide(
        @JsonProperty("ptCarId") Integer ptCarId,
        @JsonProperty("uicCode") String uicCode,
        @JsonProperty("departureSide") String departureSide
) {
}
