package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Full detail of a single train run (all its stops, current position, real-time status), as returned by
 * {@link nl.ovdata.nmbs.journey.JourneyService#journeyDetail(String)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JourneyDetailResponse(
        @JsonProperty("dayOfOperation") LocalDate dayOfOperation,
        @JsonProperty("Stops") Leg.StopsWrapper stopsWrapper,
        @JsonProperty("JourneyStatus") String journeyStatus,
        @JsonProperty("OperationalInfo") List<OperationalInfo> operationalInfo,
        @JsonProperty("ServiceDays") List<ServiceDay> serviceDays,
        @JsonProperty("LastPos") LatLon lastKnownPosition,
        @JsonProperty("rtLastPassRouteIdx") Integer realTimeLastPassedRouteIndex,
        @JsonProperty("ref") String ref,
        @JsonProperty("reachable") Boolean reachable,
        @JsonProperty("serverVersion") String serverVersion,
        @JsonProperty("dialectVersion") String dialectVersion,
        @JsonProperty("requestId") String requestId
) {

    /** Convenience accessor unwrapping the {@code Stops: { Stop: [...] } } envelope used by the raw API. */
    public List<PasslistStop> stops() {
        return stopsWrapper == null || stopsWrapper.stop() == null ? List.of() : stopsWrapper.stop();
    }
}
