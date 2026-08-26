package nl.ovdata.nmbs.composition.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TrainKey(
        @JsonProperty("trainNumber") int trainNumber,
        @JsonProperty("circulationType") String circulationType,
        @JsonProperty("discriminator") Integer discriminator,
        @JsonProperty("departureDate") LocalDateTime departureDate
) {
}
