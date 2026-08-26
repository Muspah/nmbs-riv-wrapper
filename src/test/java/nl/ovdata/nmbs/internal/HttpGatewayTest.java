package nl.ovdata.nmbs.internal;

import nl.ovdata.nmbs.NmbsApiException;
import nl.ovdata.nmbs.NmbsClientConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpGatewayTest {

    private FakeHttpServer server;
    private HttpGateway http;

    @BeforeEach
    void setUp() {
        server = new FakeHttpServer();
        http = new HttpGateway(testConfig().rivBaseUrl(server.baseUrl()).build());
    }

    private static NmbsClientConfig.Builder testConfig() {
        return NmbsClientConfig.builder()
                .rivLegacyApiKey("test-legacy-key")
                .rivJourneyApiKey("test-journey-key")
                .labApiKey("test-lab-key")
                .labBasicAuth("test-lab-basic-auth")
                .userAgent("test-user-agent");
    }

    @AfterEach
    void tearDown() throws Exception {
        http.close();
        server.close();
    }

    @Test
    void nonSuccessStatus_raisesHttpStatusException() {
        server.respondWith(404, "{\"error\":\"not found\"}");

        assertThatThrownBy(() -> http.getRaw(server.baseUrl(), "/missing", Map.of(), Map.of()))
                .isInstanceOf(NmbsApiException.HttpStatus.class)
                .satisfies(e -> {
                    NmbsApiException.HttpStatus status = (NmbsApiException.HttpStatus) e;
                    assertThat(status.statusCode()).isEqualTo(404);
                    assertThat(status.responseBody()).contains("not found");
                });
    }

    @Test
    void invalidJson_raisesParsingException() {
        server.respondWith(200, "not json");

        assertThatThrownBy(() -> http.getJson(server.baseUrl(), "/x", Map.of(), Map.of(), String[].class))
                .isInstanceOf(NmbsApiException.Parsing.class);
    }

    @Test
    void unreachableHost_raisesTransportException() throws Exception {
        HttpGateway broken = new HttpGateway(testConfig()
                .responseTimeout(java.time.Duration.ofSeconds(2))
                .connectTimeout(java.time.Duration.ofSeconds(2))
                .build());
        try {
            assertThatThrownBy(() -> broken.getRaw("http://127.0.0.1:1", "/x", Map.of(), Map.of()))
                    .isInstanceOf(NmbsApiException.Transport.class);
        } finally {
            broken.close();
        }
    }

    @Test
    void queryParametersAreUrlEncoded() {
        server.respondWith(200, "{}");
        Map<String, String> query = new HashMap<>();
        query.put("q", "a b/c");
        query.put("skipped", null);

        http.getRaw(server.baseUrl(), "/path", query, Map.of());

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getRawQuery()).isEqualTo("q=a%20b%2Fc");
    }
}
