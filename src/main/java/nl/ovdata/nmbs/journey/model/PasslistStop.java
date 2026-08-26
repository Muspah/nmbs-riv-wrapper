package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * One intermediate stop of a train's full stopping pattern, as included when a journey request is made with
 * {@code passlist=1} (journey planning, journey detail, and journey reconstruction endpoints).
 *
 * @param arrivalDelayMinutes   current arrival delay in minutes, or {@code null} if unknown/not yet arrived
 * @param departureDelayMinutes current departure delay in minutes, or {@code null} if unknown/not yet departed
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasslistStop(
        @JsonProperty("name") String name,
        @JsonProperty("extId") String extId,
        @JsonProperty("lon") Double lon,
        @JsonProperty("lat") Double lat,
        @JsonProperty("routeIdx") Integer routeIdx,
        @JsonProperty("arrTime") LocalTime arrivalTime,
        @JsonProperty("arrDate") LocalDate arrivalDate,
        @JsonProperty("arrTrack") String arrivalTrack,
        @JsonProperty("rtArrTime") LocalTime realTimeArrivalTime,
        @JsonProperty("rtArrDate") LocalDate realTimeArrivalDate,
        @JsonProperty("rtArrTrack") String realTimeArrivalTrack,
        @JsonProperty("ArrivalDelay") Integer arrivalDelayMinutes,
        @JsonProperty("depTime") LocalTime departureTime,
        @JsonProperty("depDate") LocalDate departureDate,
        @JsonProperty("depTrack") String departureTrack,
        @JsonProperty("rtDepTime") LocalTime realTimeDepartureTime,
        @JsonProperty("rtDepDate") LocalDate realTimeDepartureDate,
        @JsonProperty("rtDepTrack") String realTimeDepartureTrack,
        @JsonProperty("DepartureDelay") Integer departureDelayMinutes,
        @JsonProperty("depDir") String departureDirection,
        @JsonProperty("progressStatus") String progressStatus,
        @JsonProperty("arrivalStatus") String arrivalStatus,
        @JsonProperty("departureStatus") String departureStatus,
        @JsonProperty("departureSide") String departureSide,
        @JsonProperty("disembarkmentSide") String disembarkmentSide
) {
}
