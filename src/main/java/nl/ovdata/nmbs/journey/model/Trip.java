package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * One possible connection between two stations, as returned by the journey planner, journey reconstruction,
 * and (wrapped in a single-element list) train-filter lookups.
 *
 * @param reconstructionContext opaque context token; pass to
 *                              {@link nl.ovdata.nmbs.journey.JourneyService#reconstructJourney(String, java.time.LocalDate)}
 *                              to re-fetch this exact trip later, e.g. with the full stopping pattern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Trip(
        @JsonProperty("Origin") TripEndpoint origin,
        @JsonProperty("Destination") TripEndpoint destination,
        @JsonProperty("ServiceDays") List<ServiceDay> serviceDays,
        @JsonProperty("LegList") LegListWrapper legListWrapper,
        @JsonProperty("duration") String duration,
        @JsonProperty("rtDuration") String realTimeDuration,
        @JsonProperty("idx") Integer index,
        @JsonProperty("tripId") String tripId,
        @JsonProperty("ctxRecon") String reconstructionContext,
        @JsonProperty("checksum") String checksum
) {

    /** Convenience accessor unwrapping the {@code LegList: { Leg: [...] } } envelope used by the raw API. */
    public List<Leg> legs() {
        return legListWrapper == null || legListWrapper.leg() == null ? List.of() : legListWrapper.leg();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegListWrapper(@JsonProperty("Leg") List<Leg> leg) {
    }
}
