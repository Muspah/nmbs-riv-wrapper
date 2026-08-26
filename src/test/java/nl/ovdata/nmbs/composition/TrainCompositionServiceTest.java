package nl.ovdata.nmbs.composition;

import nl.ovdata.nmbs.NmbsClientConfig;
import nl.ovdata.nmbs.TestFixtures;
import nl.ovdata.nmbs.composition.model.MaterialUnit;
import nl.ovdata.nmbs.composition.model.TrainComposition;
import nl.ovdata.nmbs.internal.FakeHttpServer;
import nl.ovdata.nmbs.internal.HttpGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class TrainCompositionServiceTest {

    private static final String TEST_RIV_LEGACY_API_KEY = "test-legacy-key";
    private static final String TEST_LAB_API_KEY = "test-lab-key";
    private static final String TEST_LAB_BASIC_AUTH =
            Base64.getEncoder().encodeToString("test-user:test-pass".getBytes(StandardCharsets.UTF_8));

    private FakeHttpServer server;
    private HttpGateway http;
    private TrainCompositionService service;

    @BeforeEach
    void setUp() {
        server = new FakeHttpServer();
        NmbsClientConfig config = NmbsClientConfig.builder()
                .rivBaseUrl(server.baseUrl())
                .labBaseUrl(server.baseUrl())
                .rivLegacyApiKey(TEST_RIV_LEGACY_API_KEY)
                .rivJourneyApiKey("test-journey-key")
                .labApiKey(TEST_LAB_API_KEY)
                .labBasicAuth(TEST_LAB_BASIC_AUTH)
                .userAgent("test-user-agent")
                .build();
        http = new HttpGateway(config);
        service = new TrainCompositionService(http, config, "device-request-id-123");
    }

    @AfterEach
    void tearDown() throws Exception {
        http.close();
        server.close();
    }

    @Test
    void composition_viaRivEndpoint_parsesMaterialUnits() {
        server.respondWith(200, TestFixtures.load("train-composition.json"));

        TrainComposition composition = service.composition("8814209", "8833605", 4021, LocalDate.of(2026, 8, 23));

        assertThat(composition.trainKey().trainNumber()).isEqualTo(4021);
        assertThat(composition.lastPlanned()).hasSize(1);

        MaterialUnit unit = composition.lastPlanned().get(0).materialUnits().get(0);
        assertThat(unit.typeDescriptionNl()).isEqualTo("MR 08 Vehicle a");
        assertThat(unit.seatsFirstClass()).isEqualTo(16);
        assertThat(unit.seatsSecondClass()).isEqualTo(68);
        assertThat(unit.hasAirco()).isTrue();

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getPath()).isEqualTo("/api/v1.1/commercialtraincompositionsbetweenptcars");
        assertThat(request.uri().getQuery()).contains("From=8814209", "To=8833605", "TrainNumber=4021");
        assertThat(request.header("x-api-key")).isEqualTo(TEST_RIV_LEGACY_API_KEY);
        assertThat(request.header("x-device-request-id")).isEqualTo("device-request-id-123");
    }

    @Test
    void labComposition_sendsBasicAuthAndLabApiKey() {
        server.respondWith(200, TestFixtures.load("train-composition-lab.json"));

        TrainComposition composition = service.labComposition("8833001", "8822004", 2645, LocalDate.of(2026, 8, 23));

        assertThat(composition.trainKey().trainNumber()).isEqualTo(2645);
        assertThat(composition.commercialPlanned()).hasSize(1);

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getPath()).isEqualTo("/api/TrainComposition/CompositionByDate");
        assertThat(request.header("x-api-key")).isEqualTo(TEST_LAB_API_KEY);
        assertThat(request.header("Authorization")).isEqualTo("Basic " + TEST_LAB_BASIC_AUTH);

        String decoded = new String(Base64.getDecoder().decode(TEST_LAB_BASIC_AUTH), StandardCharsets.UTF_8);
        assertThat(decoded).isEqualTo("test-user:test-pass");
    }
}
