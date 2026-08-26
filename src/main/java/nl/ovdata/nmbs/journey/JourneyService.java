package nl.ovdata.nmbs.journey;

import nl.ovdata.nmbs.NmbsClientConfig;
import nl.ovdata.nmbs.internal.HttpGateway;
import nl.ovdata.nmbs.internal.IdGenerator;
import nl.ovdata.nmbs.journey.model.JourneyDetailResponse;
import nl.ovdata.nmbs.journey.model.JourneyPlanResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Journey planning, backed by the RIV journey endpoints ({@code mobile-riv.api.belgianrail.be/riv/v2/journey*}).
 */
public final class JourneyService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final HttpGateway http;
    private final NmbsClientConfig config;
    private final String deviceRequestId;

    public JourneyService(HttpGateway http, NmbsClientConfig config, String deviceRequestId) {
        this.http = http;
        this.config = config;
        this.deviceRequestId = deviceRequestId;
    }

    /**
     * Plans direct connections between two stations departing (or arriving, see {@code searchForArrival}) around
     * the given date/time.
     *
     * @param originExtId      UIC code of the origin station
     * @param destExtId        UIC code of the destination station
     * @param date             date to search on
     * @param time             time to search around
     * @param searchForArrival if {@code true}, {@code date}/{@code time} are treated as the desired arrival time
     *                          rather than departure time
     * @param forwardResults   number of connections to return after the requested time
     * @param backwardResults  number of connections to return before the requested time
     * @param includeFullStoppingPattern if {@code true}, each leg includes every intermediate stop
     */
    public JourneyPlanResponse plan(String originExtId, String destExtId, LocalDate date, LocalTime time,
                                     boolean searchForArrival, int forwardResults, int backwardResults,
                                     boolean includeFullStoppingPattern) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("originExtId", originExtId);
        query.put("destExtId", destExtId);
        query.put("lang", "en");
        query.put("date", date.format(DATE));
        query.put("time", time.format(TIME));
        query.put("searchForArrival", searchForArrival ? "1" : "0");
        query.put("originWalk", "1,,2000");
        query.put("originBike", "0");
        query.put("originCar", "0");
        query.put("destWalk", "1,,2000");
        query.put("destBike", "0");
        query.put("destCar", "0");
        query.put("minChangeTime", "0");
        query.put("products", "116");
        query.put("economic", "0");
        query.put("numF", String.valueOf(forwardResults));
        query.put("numB", String.valueOf(backwardResults));
        query.put("passlist", includeFullStoppingPattern ? "1" : "0");
        return http.getJson(config.rivBaseUrl(), "/riv/v2/journey", query, headers(), JourneyPlanResponse.class);
    }

    /**
     * Looks up connections for one specific train service between two of its stops, e.g. to check delay/platform
     * information for a train the caller already knows the number of.
     *
     * @param trainFilter product code plus train number as shown by the API, e.g. {@code "S194021"} for train S19 4021
     * @param originExtId UIC code of the boarding station
     * @param destExtId   UIC code of the alighting station
     * @param date        date to search on, or {@code null} to let the API default to today
     * @param time        time to search around
     */
    public JourneyPlanResponse planForTrain(String trainFilter, String originExtId, String destExtId,
                                             LocalDate date, LocalTime time) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("trainFilter", trainFilter);
        query.put("originExtId", originExtId);
        query.put("destExtId", destExtId);
        query.put("date", date == null ? "" : date.format(DATE));
        query.put("time", time.format(TIME));
        query.put("lang", "en");
        return http.getJson(config.rivBaseUrl(), "/riv/v2/journey", query, headers(), JourneyPlanResponse.class);
    }

    /**
     * Fetches the full detail (all stops, current position, real-time status) of a single train run.
     *
     * @param journeyDetailRef opaque reference obtained from {@link nl.ovdata.nmbs.journey.model.Leg#journeyDetailRef()}
     */
    public JourneyDetailResponse journeyDetail(String journeyDetailRef) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("id", journeyDetailRef);
        query.put("lang", "en");
        return http.getJson(config.rivBaseUrl(), "/riv/v2/journey/detail", query, headers(), JourneyDetailResponse.class);
    }

    /**
     * Re-fetches a previously planned trip, typically to obtain its full stopping pattern.
     *
     * @param reconstructionContext opaque token from {@link nl.ovdata.nmbs.journey.model.Trip#reconstructionContext()}
     * @param date                   date the trip runs on
     */
    public JourneyPlanResponse reconstructJourney(String reconstructionContext, LocalDate date) {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("ctx", reconstructionContext);
        query.put("date", date.format(DATE));
        query.put("lang", "en");
        query.put("passlist", "1");
        return http.getJson(config.rivBaseUrl(), "/riv/v2/journey/recon", query, headers(), JourneyPlanResponse.class);
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Accept", "application/json; charset=utf-8");
        headers.put("x-correlation-id", IdGenerator.next());
        headers.put("x-api-key", config.rivJourneyApiKey());
        headers.put("Accept-Language", config.acceptLanguage());
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("e_platform", "mobile");
        headers.put("User-Agent", config.userAgent());
        headers.put("x-device-request-id", deviceRequestId);
        headers.put("x-session", "------NO_USER------");
        return headers;
    }
}
