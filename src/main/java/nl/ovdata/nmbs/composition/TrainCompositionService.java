package nl.ovdata.nmbs.composition;

import nl.ovdata.nmbs.NmbsClientConfig;
import nl.ovdata.nmbs.composition.model.TrainComposition;
import nl.ovdata.nmbs.internal.HttpGateway;
import nl.ovdata.nmbs.internal.IdGenerator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rolling-stock composition lookups for a train between two of its stops.
 *
 * <p>Two independent, differently-hosted endpoints expose this data: the RIV endpoint
 * ({@link #composition}, {@code mobile-riv.api.belgianrail.be}) and the "Lab" endpoint
 * ({@link #labComposition}, {@code mobile-labapp.api.belgiantrain.be}). Both return the same
 * {@link TrainComposition} shape; in the observed traffic the Lab endpoint is the one used for domestic IC/S
 * services while the RIV endpoint is used for others, but either can be tried.</p>
 */
public final class TrainCompositionService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final HttpGateway http;
    private final NmbsClientConfig config;
    private final String deviceRequestId;

    public TrainCompositionService(HttpGateway http, NmbsClientConfig config, String deviceRequestId) {
        this.http = http;
        this.config = config;
        this.deviceRequestId = deviceRequestId;
    }

    /**
     * Fetches the rolling-stock composition via the RIV endpoint.
     *
     * @param fromUicCode   UIC code of the boarding station
     * @param toUicCode     UIC code of the alighting station
     * @param trainNumber   train number, e.g. {@code 4021}
     * @param date          date the train runs on
     */
    public TrainComposition composition(String fromUicCode, String toUicCode, int trainNumber, LocalDate date) {
        Map<String, String> query = query(fromUicCode, toUicCode, trainNumber, date);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("User-Agent", config.userAgent());
        headers.put("x-api-key", config.rivLegacyApiKey());
        headers.put("x-device-request-id", deviceRequestId);
        headers.put("Accept", "application/json; charset=utf-8");
        headers.put("x-correlation-id", IdGenerator.next());
        headers.put("Accept-Language", config.acceptLanguage());
        headers.put("e_platform", "mobile");
        return http.getJson(config.rivBaseUrl(), "/api/v1.1/commercialtraincompositionsbetweenptcars", query, headers,
                TrainComposition.class);
    }

    /**
     * Fetches the rolling-stock composition via the "Lab" endpoint.
     *
     * @param fromUicCode   UIC code of the boarding station
     * @param toUicCode     UIC code of the alighting station
     * @param trainNumber   train number, e.g. {@code 2645}
     * @param date          date the train runs on
     */
    public TrainComposition labComposition(String fromUicCode, String toUicCode, int trainNumber, LocalDate date) {
        Map<String, String> query = query(fromUicCode, toUicCode, trainNumber, date);
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "application/json; charset=utf-8");
        headers.put("Authorization", "Basic " + config.labBasicAuth());
        headers.put("x-correlation-id", IdGenerator.next());
        headers.put("x-api-key", config.labApiKey());
        headers.put("Accept-Language", config.acceptLanguage());
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("e_platform", "mobile");
        headers.put("User-Agent", config.userAgent());
        return http.getJson(config.labBaseUrl(), "/api/TrainComposition/CompositionByDate", query, headers,
                TrainComposition.class);
    }

    private static Map<String, String> query(String fromUicCode, String toUicCode, int trainNumber, LocalDate date) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("From", fromUicCode);
        query.put("To", toUicCode);
        query.put("TrainNumber", String.valueOf(trainNumber));
        query.put("IncludeMaterialInfo", "true");
        query.put("Date", date.format(DATE));
        query.put("lang", "en");
        return query;
    }
}
