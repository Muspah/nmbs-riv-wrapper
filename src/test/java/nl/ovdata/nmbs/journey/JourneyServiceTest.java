package nl.ovdata.nmbs.journey;

import nl.ovdata.nmbs.NmbsClientConfig;
import nl.ovdata.nmbs.TestFixtures;
import nl.ovdata.nmbs.internal.FakeHttpServer;
import nl.ovdata.nmbs.internal.HttpGateway;
import nl.ovdata.nmbs.journey.model.JourneyDetailResponse;
import nl.ovdata.nmbs.journey.model.JourneyPlanResponse;
import nl.ovdata.nmbs.journey.model.Leg;
import nl.ovdata.nmbs.journey.model.PasslistStop;
import nl.ovdata.nmbs.journey.model.Trip;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class JourneyServiceTest {

    private static final String TEST_JOURNEY_API_KEY = "test-journey-key";

    private FakeHttpServer server;
    private HttpGateway http;
    private JourneyService service;

    @BeforeEach
    void setUp() {
        server = new FakeHttpServer();
        NmbsClientConfig config = NmbsClientConfig.builder()
                .rivBaseUrl(server.baseUrl())
                .rivLegacyApiKey("test-legacy-key")
                .rivJourneyApiKey(TEST_JOURNEY_API_KEY)
                .labApiKey("test-lab-key")
                .labBasicAuth("test-lab-basic-auth")
                .userAgent("test-user-agent")
                .build();
        http = new HttpGateway(config);
        service = new JourneyService(http, config, "device-request-id-123");
    }

    @AfterEach
    void tearDown() throws Exception {
        http.close();
        server.close();
    }

    @Test
    void plan_parsesTripAndSendsRequiredHeaders() {
        server.respondWith(200, TestFixtures.load("journey-plan.json"));

        JourneyPlanResponse response = service.plan("8814209", "8833001", LocalDate.of(2026, 8, 23),
                LocalTime.of(23, 12, 42), false, 6, 0, true);

        assertThat(response.trips()).hasSize(1);
        Trip trip = response.trips().get(0);
        assertThat(trip.origin().name()).isEqualTo("Nivelles / Nijvel");
        assertThat(trip.destination().name()).isEqualTo("Louvain / Leuven");
        assertThat(trip.legs()).hasSize(1);

        Leg leg = trip.legs().get(0);
        assertThat(leg.products()).hasSize(1);
        assertThat(leg.products().get(0).number()).isEqualTo("4021");
        assertThat(leg.journeyDetailRef().ref()).isEqualTo("1|9570|0|80|23082026");

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getPath()).isEqualTo("/riv/v2/journey");
        assertThat(request.uri().getQuery()).contains("originExtId=8814209", "destExtId=8833001", "passlist=1");
        assertThat(request.header("x-api-key")).isEqualTo(TEST_JOURNEY_API_KEY);
        assertThat(request.header("x-device-request-id")).isEqualTo("device-request-id-123");
        assertThat(request.header("x-session")).isEqualTo("------NO_USER------");
        assertThat(request.header("e_platform")).isEqualTo("mobile");
    }

    @Test
    void journeyDetail_parsesFullStoppingPattern() {
        server.respondWith(200, TestFixtures.load("journey-detail.json"));

        JourneyDetailResponse detail = service.journeyDetail("1|9570|0|80|23082026");

        assertThat(detail.ref()).isEqualTo("1|9570|0|80|23082026");
        assertThat(detail.dayOfOperation()).isEqualTo(LocalDate.of(2026, 8, 23));
        assertThat(detail.stops()).hasSize(2);

        PasslistStop last = detail.stops().get(1);
        assertThat(last.name()).isEqualTo("Louvain / Leuven");
        assertThat(last.arrivalDelayMinutes()).isZero();

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getPath()).isEqualTo("/riv/v2/journey/detail");
        assertThat(request.uri().getRawQuery()).isEqualTo("id=1%7C9570%7C0%7C80%7C23082026&lang=en");
    }

    @Test
    void reconstructJourney_parsesPasslistOnTheLeg() {
        server.respondWith(200, TestFixtures.load("journey-recon.json"));

        JourneyPlanResponse response = service.reconstructJourney("some-context-token", LocalDate.of(2026, 8, 23));

        Leg leg = response.trips().get(0).legs().get(0);
        assertThat(leg.stops()).hasSize(3);
        assertThat(leg.stops().get(0).departureDirection()).isEqualTo("Landen");

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getPath()).isEqualTo("/riv/v2/journey/recon");
        assertThat(request.uri().getQuery()).contains("ctx=some-context-token", "passlist=1");
    }

    @Test
    void planForTrain_omitsDateWhenNull() {
        server.respondWith(200, TestFixtures.load("journey-plan.json"));

        service.planForTrain("S194021", "8814209", "8833001", null, LocalTime.of(23, 10, 11));

        FakeHttpServer.RecordedRequest request = server.lastRequest();
        assertThat(request.uri().getQuery()).contains("trainFilter=S194021", "date=&");
    }
}
