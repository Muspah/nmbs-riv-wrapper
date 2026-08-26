package nl.ovdata.nmbs.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Builds the {@link ObjectMapper} used to read NMBS API responses.
 *
 * <p>The API mixes several ad-hoc date/time representations across endpoints (e.g. {@code "2026-08-23 23:07:00"}
 * for planned times but {@code "00:02:00"} for a delay expressed as a duration). Dedicated deserializers below
 * translate those into standard {@code java.time} types so consumers never have to parse strings themselves.</p>
 */
public final class JsonSupport {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private JsonSupport() {
    }

    public static ObjectMapper newMapper() {
        SimpleModule module = new SimpleModule("nmbs-api-wrapper");
        module.addDeserializer(LocalDateTime.class, new ApiLocalDateTimeDeserializer());
        module.addDeserializer(LocalDate.class, new ApiLocalDateDeserializer());
        module.addDeserializer(LocalTime.class, new ApiLocalTimeDeserializer());
        module.addDeserializer(Duration.class, new ApiDurationDeserializer());

        return new ObjectMapper()
                .registerModule(module)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
    }

    /** Parses {@code "yyyy-MM-dd HH:mm:ss"}, as used for planned/expected/real-time timestamps. */
    static final class ApiLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            return LocalDateTime.parse(text.trim(), DATE_TIME);
        }
    }

    /** Parses {@code "yyyy-MM-dd"}, as used for standalone dates such as {@code dayOfOperation}. */
    static final class ApiLocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            return LocalDate.parse(text.trim(), DATE);
        }
    }

    /** Parses {@code "HH:mm:ss"}, as used for scheduled/real-time times within a journey leg. */
    static final class ApiLocalTimeDeserializer extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            return LocalTime.parse(text.trim());
        }
    }

    /**
     * Parses a delay expressed as {@code "HH:mm:ss"} (live board endpoints) into a {@link Duration}.
     * Journey-planner endpoints instead express delays as a plain integer number of minutes; fields backed by
     * that shape are typed as {@code Integer} in the corresponding model and do not use this deserializer.
     */
    static final class ApiDurationDeserializer extends JsonDeserializer<Duration> {
        @Override
        public Duration deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            String text = p.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }
            String[] parts = text.trim().split(":");
            if (parts.length != 3) {
                throw new IOException("Unexpected duration format: " + text);
            }
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = Long.parseLong(parts[2]);
            return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        }
    }
}
