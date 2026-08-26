package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * One leg of a {@link Trip}: travel on a single train service between two stations.
 *
 * <p>The NMBS journey planner only ever returns direct connections (no transfers) for domestic routes, so a
 * {@link Trip} in practice always has exactly one leg; the API still models it as a list.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Leg(
        @JsonProperty("Origin") TripEndpoint origin,
        @JsonProperty("Destination") TripEndpoint destination,
        @JsonProperty("JourneyStatus") String journeyStatus,
        @JsonProperty("Product") List<Product> products,
        @JsonProperty("JourneyDetailRef") JourneyDetailRef journeyDetailRef,
        @JsonProperty("Stops") StopsWrapper stopsWrapper,
        @JsonProperty("idx") Integer index,
        @JsonProperty("name") String name,
        @JsonProperty("number") String number,
        @JsonProperty("category") String category,
        @JsonProperty("type") String type,
        @JsonProperty("reachable") Boolean reachable,
        @JsonProperty("direction") String direction,
        @JsonProperty("duration") String duration,
        @JsonProperty("OperationalInfo") List<OperationalInfo> operationalInfo
) {

    /**
     * The full intermediate stopping pattern, present only when the request set {@code passlist=1}; unwraps the
     * {@code Stops: { Stop: [...] } } envelope used by the raw API.
     *
     * @return the stops, or an empty list if the request did not ask for the full stopping pattern
     */
    public List<PasslistStop> stops() {
        return stopsWrapper == null ? List.of() : stopsWrapper.stop() == null ? List.of() : stopsWrapper.stop();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StopsWrapper(@JsonProperty("Stop") List<PasslistStop> stop) {
    }
}
