package nl.ovdata.nmbs.composition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The rolling-stock composition between two consecutive stops of a train.
 *
 * @param lastUpdateDateTime when this segment's composition was last confirmed; {@code 0001-01-01 00:00:00} for
 *                            purely planned (not yet confirmed) segments
 * @param confirmedBy         source of the data, e.g. {@code "IDA"} for a live/confirmed composition or
 *                            {@code "Mistral"} for a planning-system prediction
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CompositionSegment(
        @JsonProperty("ptCarFrom") PtCar from,
        @JsonProperty("ptCarTo") PtCar to,
        @JsonProperty("departureSides") List<DepartureSide> departureSides,
        @JsonProperty("compositionSegmentId") String compositionSegmentId,
        @JsonProperty("lastUpdateDateTime") LocalDateTime lastUpdateDateTime,
        @JsonProperty("confirmedBy") String confirmedBy,
        @JsonProperty("materialUnits") List<MaterialUnit> materialUnits
) {
}
