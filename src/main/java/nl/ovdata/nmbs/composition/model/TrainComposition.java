package nl.ovdata.nmbs.composition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The rolling-stock composition of a train between two of its stops.
 *
 * @param lastPlanned      the most up-to-date, confirmed composition segments; empty if nothing has been confirmed yet
 * @param commercialPlanned the originally planned composition segments, always present as a fallback
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TrainComposition(
        @JsonProperty("messageDateTime") String messageDateTime,
        @JsonProperty("trainKey") TrainKey trainKey,
        @JsonProperty("lastPlanned") List<CompositionSegment> lastPlanned,
        @JsonProperty("commercialPlanned") List<CompositionSegment> commercialPlanned,
        @JsonProperty("warnings") List<String> warnings
) {
}
