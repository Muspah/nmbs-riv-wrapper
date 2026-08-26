package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The train service operating a {@link Leg}, e.g. "IC 521" operated by SNCB/NMBS.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(
        @JsonProperty("name") String name,
        @JsonProperty("num") String number,
        @JsonProperty("catOut") String category,
        @JsonProperty("catCode") String categoryCode,
        @JsonProperty("operator") String operator,
        @JsonProperty("operatorCode") String operatorCode,
        @JsonProperty("displayNumber") String displayNumber
) {
}
