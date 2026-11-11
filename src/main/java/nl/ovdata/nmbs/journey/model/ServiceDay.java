package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Describes which days, within a planning period, a {@link Trip} actually operates.
 *
 * @param humanReadableSummary e.g. {@code "not every day"}
 * @param operatingDays        human-readable list of the specific dates it does run on, e.g.
 *                              {@code "23., 30. Aug, 13., 27. Sep, 29. Nov"}
 * @param bitmask               raw operating-days bitmask as sent by the API; most significant bit first, bit i = {@code planningPeriodBegin} + i days; see {@link #operatingDays(LocalDate, LocalDate)}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceDay(
        @JsonProperty("planningPeriodBegin") LocalDate planningPeriodBegin,
        @JsonProperty("planningPeriodEnd") LocalDate planningPeriodEnd,
        @JsonProperty("sDaysR") String humanReadableSummary,
        @JsonProperty("sDaysI") String operatingDays,
        @JsonProperty("sDaysB") String bitmask
) {

    /**
     * Decodes {@link #bitmask()}: days before the planning period or past the end of the bitmask are not set.
     *
     * @return the days in [from, to] that are set, ascending
     * @throws IllegalArgumentException if the bitmask contains a non-hex character
     */
    public List<LocalDate> operatingDays(LocalDate from, LocalDate to) {
        List<LocalDate> days = new ArrayList<>();
        for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
            long i = ChronoUnit.DAYS.between(planningPeriodBegin, day);
            if (i < 0 || i / 4 >= bitmask.length()) {
                continue;
            }
            int nibble = Character.digit(bitmask.charAt((int) (i / 4)), 16);
            if (nibble < 0) {
                throw new IllegalArgumentException("sDaysB is not hex: " + bitmask);
            }
            if ((nibble >> (3 - i % 4) & 1) == 1) {
                days.add(day);
            }
        }
        return days;
    }
}
