package nl.ovdata.nmbs;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration for {@link NmbsClient}.
 *
 * <p>The NMBS/SNCB mobile backend is not a publicly documented, keyed API. This library does not ship any of the
 * credentials (API keys, {@code Authorization: Basic} value) or the {@code User-Agent} the official "BelgianTrain"
 * app uses to talk to it — those must be supplied explicitly via {@link #builder()}. See the {@code contrib/}
 * directory in this repository for how to capture your own values from the app's traffic.</p>
 *
 * <p>This is an unofficial client. It is not affiliated with, endorsed by, or supported by NMBS/SNCB.</p>
 */
public final class NmbsClientConfig {

    public static final String DEFAULT_RIV_BASE_URL = "https://mobile-riv.api.belgianrail.be";
    public static final String DEFAULT_LAB_BASE_URL = "https://mobile-labapp.api.belgiantrain.be";

    public static final String DEFAULT_ACCEPT_LANGUAGE = "en-GB,en;q=0.9";

    private final String rivLegacyApiKey;
    private final String rivJourneyApiKey;
    private final String labApiKey;
    private final String labBasicAuth;
    private final String rivBaseUrl;
    private final String labBaseUrl;
    private final String userAgent;
    private final String acceptLanguage;
    private final Duration connectTimeout;
    private final Duration responseTimeout;

    private NmbsClientConfig(Builder builder) {
        this.rivLegacyApiKey = builder.rivLegacyApiKey;
        this.rivJourneyApiKey = builder.rivJourneyApiKey;
        this.labApiKey = builder.labApiKey;
        this.labBasicAuth = builder.labBasicAuth;
        this.rivBaseUrl = builder.rivBaseUrl;
        this.labBaseUrl = builder.labBaseUrl;
        this.userAgent = builder.userAgent;
        this.acceptLanguage = builder.acceptLanguage;
        this.connectTimeout = builder.connectTimeout;
        this.responseTimeout = builder.responseTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String rivLegacyApiKey() {
        return rivLegacyApiKey;
    }

    public String rivJourneyApiKey() {
        return rivJourneyApiKey;
    }

    public String labApiKey() {
        return labApiKey;
    }

    public String labBasicAuth() {
        return labBasicAuth;
    }

    public String rivBaseUrl() {
        return rivBaseUrl;
    }

    public String labBaseUrl() {
        return labBaseUrl;
    }

    public String userAgent() {
        return userAgent;
    }

    public String acceptLanguage() {
        return acceptLanguage;
    }

    public Duration connectTimeout() {
        return connectTimeout;
    }

    public Duration responseTimeout() {
        return responseTimeout;
    }

    public static final class Builder {
        private String rivLegacyApiKey;
        private String rivJourneyApiKey;
        private String labApiKey;
        private String labBasicAuth;
        private String rivBaseUrl = DEFAULT_RIV_BASE_URL;
        private String labBaseUrl = DEFAULT_LAB_BASE_URL;
        private String userAgent;
        private String acceptLanguage = DEFAULT_ACCEPT_LANGUAGE;
        private Duration connectTimeout = Duration.ofSeconds(10);
        private Duration responseTimeout = Duration.ofSeconds(15);

        private Builder() {
        }

        /** Sets the {@code x-api-key} used for the live board and RIV train-composition endpoints. Required. */
        public Builder rivLegacyApiKey(String rivLegacyApiKey) {
            this.rivLegacyApiKey = Objects.requireNonNull(rivLegacyApiKey, "rivLegacyApiKey");
            return this;
        }

        /** Sets the {@code x-api-key} used for the journey-planner endpoints. Required. */
        public Builder rivJourneyApiKey(String rivJourneyApiKey) {
            this.rivJourneyApiKey = Objects.requireNonNull(rivJourneyApiKey, "rivJourneyApiKey");
            return this;
        }

        /** Sets the {@code x-api-key} used for the "Lab" train-composition endpoint. Required. */
        public Builder labApiKey(String labApiKey) {
            this.labApiKey = Objects.requireNonNull(labApiKey, "labApiKey");
            return this;
        }

        /** Sets the base64 {@code Authorization: Basic} value used for the "Lab" train-composition endpoint. Required. */
        public Builder labBasicAuth(String labBasicAuth) {
            this.labBasicAuth = Objects.requireNonNull(labBasicAuth, "labBasicAuth");
            return this;
        }

        /** Overrides the base URL for {@code mobile-riv.api.belgianrail.be} (live board, journey, RIV composition). */
        public Builder rivBaseUrl(String rivBaseUrl) {
            this.rivBaseUrl = Objects.requireNonNull(rivBaseUrl, "rivBaseUrl");
            return this;
        }

        /** Overrides the base URL for {@code mobile-labapp.api.belgiantrain.be} (Lab composition). */
        public Builder labBaseUrl(String labBaseUrl) {
            this.labBaseUrl = Objects.requireNonNull(labBaseUrl, "labBaseUrl");
            return this;
        }

        /** Sets the {@code User-Agent} sent with every request. Required. */
        public Builder userAgent(String userAgent) {
            this.userAgent = Objects.requireNonNull(userAgent, "userAgent");
            return this;
        }

        public Builder acceptLanguage(String acceptLanguage) {
            this.acceptLanguage = Objects.requireNonNull(acceptLanguage, "acceptLanguage");
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = Objects.requireNonNull(connectTimeout, "connectTimeout");
            return this;
        }

        public Builder responseTimeout(Duration responseTimeout) {
            this.responseTimeout = Objects.requireNonNull(responseTimeout, "responseTimeout");
            return this;
        }

        public NmbsClientConfig build() {
            requireSet(rivLegacyApiKey, "rivLegacyApiKey");
            requireSet(rivJourneyApiKey, "rivJourneyApiKey");
            requireSet(labApiKey, "labApiKey");
            requireSet(labBasicAuth, "labBasicAuth");
            requireSet(userAgent, "userAgent");
            return new NmbsClientConfig(this);
        }

        private static void requireSet(String value, String field) {
            if (value == null) {
                throw new IllegalStateException(
                        field + " must be set via the builder; this library ships no default credentials. "
                                + "See the contrib/ directory for how to capture your own values.");
            }
        }
    }
}
