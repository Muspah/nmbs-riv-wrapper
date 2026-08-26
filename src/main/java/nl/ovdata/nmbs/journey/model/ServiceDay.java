package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Describes which days, within a planning period, a {@link Trip} actually operates.
 *
 * @param humanReadableSummary e.g. {@code "not every day"}
 * @param operatingDays        human-readable list of the specific dates it does run on, e.g.
 *                              {@code "23., 30. Aug, 13., 27. Sep, 29. Nov"}
 * @param bitmask               raw operating-days bitmask as sent by the API; not decoded by this library
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceDay(
        @JsonProperty("planningPeriodBegin") LocalDate planningPeriodBegin,
        @JsonProperty("planningPeriodEnd") LocalDate planningPeriodEnd,
        @JsonProperty("sDaysR") String humanReadableSummary,
        @JsonProperty("sDaysI") String operatingDays,
        @JsonProperty("sDaysB") String bitmask
) {
}
