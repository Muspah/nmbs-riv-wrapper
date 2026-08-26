package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The origin or destination of a {@link Trip} or {@link Leg}: a station plus the (real-time) time of
 * departure/arrival there.
 *
 * @param time      scheduled local time, e.g. {@code "21:45:00"}
 * @param date      scheduled date this stop falls on
 * @param track     scheduled platform/track
 * @param realTimeTime real-time local time, or {@code null} if no real-time data is available
 * @param realTimeDate real-time date
 * @param realTimeTrack real-time platform/track, if it differs from the scheduled one
 * @param delayMinutes  current delay in minutes, or {@code null} if unknown
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TripEndpoint(
        @JsonProperty("name") String name,
        @JsonProperty("extId") String extId,
        @JsonProperty("lon") Double lon,
        @JsonProperty("lat") Double lat,
        @JsonProperty("routeIdx") Integer routeIdx,
        @JsonProperty("time") LocalTime time,
        @JsonProperty("date") LocalDate date,
        @JsonProperty("track") String track,
        @JsonProperty("rtTime") LocalTime realTimeTime,
        @JsonProperty("rtDate") LocalDate realTimeDate,
        @JsonProperty("rtTrack") String realTimeTrack,
        @JsonProperty("Delay") Integer delayMinutes,
        @JsonProperty("progressStatus") String progressStatus,
        @JsonProperty("departureStatus") String departureStatus,
        @JsonProperty("arrivalStatus") String arrivalStatus
) {
}
