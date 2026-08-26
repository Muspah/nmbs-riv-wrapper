package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response of the journey planner, journey reconstruction, and train-filter endpoints: one or more candidate
 * {@link Trip Trips} between an origin and a destination.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneyPlanResponse(
        @JsonProperty("Trip") List<Trip> trips,
        @JsonProperty("serverVersion") String serverVersion,
        @JsonProperty("dialectVersion") String dialectVersion,
        @JsonProperty("requestId") String requestId
) {
}
