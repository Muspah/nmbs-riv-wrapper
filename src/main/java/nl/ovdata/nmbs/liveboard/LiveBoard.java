package nl.ovdata.nmbs.liveboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * A departure or arrival board for one station, as returned by the {@code dacs} endpoint.
 *
 * @param entries individual departures or arrivals, in the order the API returned them (typically by time)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record LiveBoard<T>(
        @JsonProperty("requestDate") String requestDate,
        @JsonProperty("lastUpdate") String lastUpdate,
        @JsonProperty("count") int count,
        @JsonProperty("entries") List<T> entries
) {
}
