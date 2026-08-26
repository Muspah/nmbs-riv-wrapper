package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Opaque reference to a leg's full detail/stopping pattern, to be passed to
 * {@link nl.ovdata.nmbs.journey.JourneyService#journeyDetail(String)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneyDetailRef(
        @JsonProperty("ref") String ref
) {
}
