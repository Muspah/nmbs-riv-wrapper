package nl.ovdata.nmbs;

import nl.ovdata.nmbs.composition.TrainCompositionService;
import nl.ovdata.nmbs.internal.HttpGateway;
import nl.ovdata.nmbs.internal.IdGenerator;
import nl.ovdata.nmbs.journey.JourneyService;
import nl.ovdata.nmbs.liveboard.LiveBoardService;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Entry point of the library: a thread-safe client for the unofficial NMBS/SNCB (Belgian Rail) mobile backend.
 *
 * <p>This talks to the same undocumented HTTP APIs the official "BelgianTrain" mobile app uses, reverse-engineered
 * from its own traffic. It is not affiliated with, endorsed by, or supported by NMBS/SNCB, and may break without
 * notice if NMBS changes the backend. This library ships no default credentials or {@code User-Agent}; see
 * {@link NmbsClientConfig} for how to supply your own via {@link NmbsClientConfig#builder()}.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * NmbsClientConfig config = NmbsClientConfig.builder()
 *         .rivLegacyApiKey("...")
 *         .rivJourneyApiKey("...")
 *         .labApiKey("...")
 *         .labBasicAuth("...")
 *         .userAgent("...")
 *         .build();
 *
 * try (NmbsClient client = NmbsClient.create(config)) {
 *     LiveBoard<DepartureEntry> board = client.liveBoard().departures("8833001", LocalDateTime.now(), 20);
 *     board.entries().forEach(d -> System.out.println(d.trainNumber() + " -> " + d.destinationNl()));
 * }
 * }</pre>
 *
 * <p>Instances hold on to an HTTP connection pool and should be reused for the lifetime of the application and
 * {@link #close() closed} on shutdown, rather than created per request.</p>
 */
public final class NmbsClient implements Closeable {

    private final HttpGateway http;
    private final LiveBoardService liveBoard;
    private final JourneyService journey;
    private final TrainCompositionService trainComposition;

    private NmbsClient(NmbsClientConfig config) {
        this.http = new HttpGateway(config);
        String deviceRequestId = IdGenerator.next();
        this.liveBoard = new LiveBoardService(http, config);
        this.journey = new JourneyService(http, config, deviceRequestId);
        this.trainComposition = new TrainCompositionService(http, config, deviceRequestId);
    }

    /** Creates a client with the given configuration, including credentials (see {@link NmbsClientConfig}). */
    public static NmbsClient create(NmbsClientConfig config) {
        return new NmbsClient(config);
    }

    /** Live departure and arrival boards. */
    public LiveBoardService liveBoard() {
        return liveBoard;
    }

    /** Journey planning, journey detail, and journey reconstruction. */
    public JourneyService journey() {
        return journey;
    }

    /** Rolling-stock (train) composition lookups. */
    public TrainCompositionService trainComposition() {
        return trainComposition;
    }

    @Override
    public void close() {
        try {
            http.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
