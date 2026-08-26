package nl.ovdata.nmbs.liveboard;

import com.fasterxml.jackson.databind.JavaType;
import nl.ovdata.nmbs.NmbsClientConfig;
import nl.ovdata.nmbs.internal.HttpGateway;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Live departure and arrival boards for a station, backed by the {@code dacs} endpoint
 * ({@code mobile-riv.api.belgianrail.be}).
 */
public final class LiveBoardService {

    private static final DateTimeFormatter FROM_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String PATH = "/api/v1.0/dacs";

    private final HttpGateway http;
    private final NmbsClientConfig config;

    public LiveBoardService(HttpGateway http, NmbsClientConfig config) {
        this.http = http;
        this.config = config;
    }

    /**
     * Fetches the live departure board for a station.
     *
     * @param uicCode UIC station code, e.g. {@code "8833001"} for Leuven
     * @param from    only entries from this point in time onward are returned
     * @param count   maximum number of entries to return
     */
    public LiveBoard<DepartureEntry> departures(String uicCode, LocalDateTime from, int count) {
        JavaType type = http.objectMapper().getTypeFactory()
                .constructParametricType(LiveBoard.class, DepartureEntry.class);
        return http.getJson(config.rivBaseUrl(), PATH, query("DeparturesApp", uicCode, from, count), headers(), type);
    }

    /**
     * Fetches the live arrival board for a station.
     *
     * @param uicCode UIC station code, e.g. {@code "8833001"} for Leuven
     * @param from    only entries from this point in time onward are returned
     * @param count   maximum number of entries to return
     */
    public LiveBoard<ArrivalEntry> arrivals(String uicCode, LocalDateTime from, int count) {
        JavaType type = http.objectMapper().getTypeFactory()
                .constructParametricType(LiveBoard.class, ArrivalEntry.class);
        return http.getJson(config.rivBaseUrl(), PATH, query("ArrivalsApp", uicCode, from, count), headers(), type);
    }

    private static Map<String, String> query(String queryType, String uicCode, LocalDateTime from, int count) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("query", queryType);
        query.put("UicCode", uicCode);
        query.put("FromDate", from.format(FROM_DATE));
        query.put("Count", String.valueOf(count));
        return query;
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("x-api-key", config.rivLegacyApiKey());
        headers.put("Accept", "*/*");
        headers.put("User-Agent", config.userAgent());
        headers.put("Accept-Language", config.acceptLanguage());
        return headers;
    }
}
