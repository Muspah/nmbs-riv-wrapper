package nl.ovdata.nmbs.liveboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A single arrival on a station's live arrival board.
 *
 * @param platformChanged {@code true} if the platform was changed since the schedule was first published
 * @param expectedArrival real-time expected arrival, or {@code null} if the train is running on schedule
 *                        and no real-time estimate was published
 * @param arrivalDelay    current delay, or {@code null} if none is known/reported
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ArrivalEntry(
        @JsonProperty("EntryDate") LocalDateTime entryDate,
        @JsonProperty("UicCode") String uicCode,
        @JsonProperty("TrainNumber") int trainNumber,
        @JsonProperty("CommercialType") String commercialType,
        @JsonProperty("PlannedArrival") LocalDateTime plannedArrival,
        @JsonProperty("OriginNl") String originNl,
        @JsonProperty("OriginFr") String originFr,
        @JsonProperty("Status") String status,
        @JsonProperty("Platform") String platform,
        @JsonProperty("PlatformChanged") Boolean platformChanged,
        @JsonProperty("ArrivalStatusNl") String arrivalStatusNl,
        @JsonProperty("ArrivalStatusFr") String arrivalStatusFr,
        @JsonProperty("ExpectedArrival") LocalDateTime expectedArrival,
        @JsonProperty("ArrivalDelay") Duration arrivalDelay,
        @JsonProperty("Origin1UicCode") String origin1UicCode
) {
}
