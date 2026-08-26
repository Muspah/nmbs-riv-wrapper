package nl.ovdata.nmbs.liveboard;

import nl.ovdata.nmbs.NmbsClientConfig;
import nl.ovdata.nmbs.TestFixtures;
import nl.ovdata.nmbs.internal.FakeHttpServer;
import nl.ovdata.nmbs.internal.HttpGateway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class LiveBoardServiceTest {

    private static final String TEST_USER_AGENT = "test-user-agent";
    private static final String TEST_ACCEPT_LANGUAGE = "nl-BE,nl;q=0.9";

    private FakeHttpServer server;
    private HttpGateway http;
    private LiveBoardService service;

    @BeforeEach
    void setUp() {
        server = new FakeHttpServer();
        NmbsClientConfig config = NmbsClientConfig.builder()
                .rivBaseUrl(server.baseUrl())
                .rivLegacyApiKey("test-legacy-key")
                .rivJourneyApiKey("test-journey-key")
                .labApiKey("test-lab-key")
                .labBasicAuth("test-lab-basic-auth")
                .userAgent(TEST_USER_AGENT)
                .acceptLanguage(TEST_ACCEPT_LANGUAGE)
                .build();
        http = new HttpGateway(config);
        service = new LiveBoardService(http, config);
    }

    @AfterEach
    void tearDown() throws Exception {
        http.close();
        server.close();
    }

    @Test
    void departures_parsesEntriesAndSendsRequiredHeaders() {
        server.respondWith(200, TestFixtures.load("departure-board.json"));

        LiveBoard<DepartureEntry> board = service.departures("8833001", LocalDateTime.of(2026, 8, 23, 23, 7, 12), 100);

        assertThat(board.count()).isEqualTo(8);
        assertThat(board.entries()).hasSize(3);

        DepartureEntry delayed = board.entries().get(2);
        assertThat(delayed.trainNumber()).isEqualTo(2644);
        assertThat(delayed.commercialType()).isEqualTo("IC");
        assertThat(delayed.destinationNl()).isEqualTo("Brussels Airport Antwerpen-Centraal");
        assertThat(delayed.platformChanged()).isTrue();
        assertThat(delayed.plannedDeparture()).isEqualTo(LocalDateTime.of(2026, 8, 23, 23, 24, 0));
        assertThat(delayed.expectedDeparture()).isEqualTo(LocalDateTime.of(2026, 8, 23, 23, 26, 0));
        assertThat(delayed.departureDelay()).isEqualTo(Duration.ofMinutes(2));

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getPath()).isEqualTo("/api/v1.0/dacs");
        assertThat(request.uri().getQuery()).contains("query=DeparturesApp", "UicCode=8833001", "Count=100");
        assertThat(request.header("x-api-key")).isEqualTo("test-legacy-key");
        assertThat(request.header("Accept")).isEqualTo("*/*");
        assertThat(request.header("User-Agent")).isEqualTo(TEST_USER_AGENT);
        assertThat(request.header("Accept-Language")).isEqualTo(TEST_ACCEPT_LANGUAGE);
    }

    @Test
    void arrivals_parsesEntries() {
        server.respondWith(200, TestFixtures.load("arrival-board.json"));

        LiveBoard<ArrivalEntry> board = service.arrivals("8833001", LocalDateTime.of(2026, 8, 23, 23, 7, 12), 100);

        assertThat(board.entries()).hasSize(3);
        ArrivalEntry first = board.entries().get(0);
        assertThat(first.trainNumber()).isEqualTo(4021);
        assertThat(first.originNl()).isEqualTo("Nijvel");
        assertThat(first.plannedArrival()).isEqualTo(LocalDateTime.of(2026, 8, 23, 23, 12, 0));
        assertThat(first.arrivalDelay()).isNull();

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getQuery()).contains("query=ArrivalsApp");
    }
}
