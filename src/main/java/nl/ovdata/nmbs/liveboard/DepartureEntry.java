package nl.ovdata.nmbs.liveboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A single departure on a station's live departure board.
 *
 * @param platformChanged {@code true} if the platform was changed since the schedule was first published
 * @param expectedDeparture real-time expected departure, or {@code null} if the train is running on schedule
 *                          and no real-time estimate was published
 * @param departureDelay    current delay, or {@code null} if none is known/reported
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DepartureEntry(
        @JsonProperty("EntryDate") LocalDateTime entryDate,
        @JsonProperty("UicCode") String uicCode,
        @JsonProperty("TrainNumber") int trainNumber,
        @JsonProperty("CommercialType") String commercialType,
        @JsonProperty("PlannedDeparture") LocalDateTime plannedDeparture,
        @JsonProperty("DestinationNl") String destinationNl,
        @JsonProperty("DestinationFr") String destinationFr,
        @JsonProperty("Status") String status,
        @JsonProperty("Platform") String platform,
        @JsonProperty("PlatformChanged") Boolean platformChanged,
        @JsonProperty("DepartureStatusNl") String departureStatusNl,
        @JsonProperty("DepartureStatusFr") String departureStatusFr,
        @JsonProperty("ExpectedDeparture") LocalDateTime expectedDeparture,
        @JsonProperty("DepartureDelay") Duration departureDelay,
        @JsonProperty("Destination1UicCode") String destination1UicCode
) {
}
