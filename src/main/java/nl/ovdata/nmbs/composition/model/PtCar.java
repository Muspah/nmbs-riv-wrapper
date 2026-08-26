package nl.ovdata.nmbs.composition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A "PT car" boundary point: a station the train passes through, identified by both an internal id and UIC code. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PtCar(
        @JsonProperty("id") Integer id,
        @JsonProperty("uicCode") String uicCode
) {
}
