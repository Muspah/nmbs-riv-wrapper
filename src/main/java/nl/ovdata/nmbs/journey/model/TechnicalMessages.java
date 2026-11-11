package nl.ovdata.nmbs.journey.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** The {@code TechnicalMessages: { TechnicalMessage: [{key, value}] } } envelope of the journey endpoints. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TechnicalMessages(@JsonProperty("TechnicalMessage") List<TechnicalMessage> messages) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TechnicalMessage(@JsonProperty("key") String key, @JsonProperty("value") String value) {
    }

    /** Raw value of the message with the given key, or null if absent. */
    public String value(String key) {
        if (messages == null) {
            return null;
        }
        return messages.stream().filter(m -> key.equals(m.key())).map(TechnicalMessage::value).findFirst().orElse(null);
    }

    /**
     * The HAFAS plan version: the {@code planid=} token of the {@code backendInfo} message (not {@code planid0=}
     * or {@code planid_adr=}).
     *
     * @return the plan id, or null if the message or token is absent
     */
    public String planId() {
        String info = value("backendInfo");
        if (info == null) {
            return null;
        }
        for (String token : info.split("\\s+")) {
            if (token.startsWith("planid=")) {
                return token.substring("planid=".length());
            }
        }
        return null;
    }
}
