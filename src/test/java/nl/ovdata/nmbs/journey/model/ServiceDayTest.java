package nl.ovdata.nmbs.journey.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceDayTest {

    private static final String L2893_BITMASK = "F9F3E00F9F007CF833E7CF8F3E7CF1F3A0001F3E7CF9F3E7CB9F3E7C89F3E000000000F9F3E7CD9F3E7CF9F3C78F9F3E7CF9F3E7CF9F3E7C79F3E7CF9D3E3CF9F3E7CF9F3E7CD9F3E7CF9F3E7CF9F3E7CF9F3E3CE9F3E7CF80";
    private static final ServiceDay L2893 = new ServiceDay(LocalDate.of(2026, 1, 5), LocalDate.of(2027, 12, 11),
            "not every day", null, L2893_BITMASK);

    private static List<LocalDate> days(ServiceDay sd, LocalDate from, LocalDate to) {
        return sd.operatingDays(from, to);
    }

    @Test
    void decodesTheCapturedBitmask() {
        assertThat(days(L2893, LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 11))).isEmpty();
        assertThat(days(L2893, LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 18)))
                .containsExactly(LocalDate.of(2026, 10, 12), LocalDate.of(2026, 10, 13), LocalDate.of(2026, 10, 14),
                        LocalDate.of(2026, 10, 15), LocalDate.of(2026, 10, 16));
        assertThat(days(L2893, LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12)))
                .containsExactly(LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 12));
    }

    @Test
    void daysOutsideTheBitmaskAreNotSet() {
        assertThat(days(L2893, LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 4))).isEmpty();
        assertThat(days(L2893, LocalDate.of(2028, 1, 1), LocalDate.of(2028, 1, 7))).isEmpty();
    }

    @Test
    void nonHexBitmaskThrows() {
        ServiceDay bad = new ServiceDay(LocalDate.of(2026, 1, 5), null, null, null, "F9G");
        assertThatThrownBy(() -> days(bad, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 1, 14)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
