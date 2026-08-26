# nmbs-api-wrapper

An unofficial Java client for the mobile NMBS/SNCB (Belgian Rail) backend APIs: live departure/arrival boards,
journey planning, and train (rolling-stock) composition.

This talks to the same undocumented HTTP APIs the official **BelgianTrain** mobile app uses. It is **not
affiliated with, endorsed by, or supported by NMBS/SNCB**. The endpoints are not publicly documented and can
change or disappear without notice. This library ships **no default credentials or `User-Agent`** — you must
supply your own, captured from the app's own traffic; see [Credentials & stability](#credentials--stability)
below.

## Requirements

- Java 17+
- Maven (or any build tool that can consume a Maven-shaped dependency)

## Installation

Build and install into your local repository:

```bash
mvn clean install
```

Then depend on it:

```xml
<dependency>
    <groupId>nl.ovdata</groupId>
    <artifactId>nmbs-api-wrapper</artifactId>
    <version>1.0.0</version>
</dependency>
```

The library depends only on Apache HttpClient5, Jackson Databind, and the SLF4J API. It brings no logging
implementation of its own — plug in whichever one (Logback, Log4j2, ...) your application already uses.

## Usage

Every credential and the `User-Agent` must be supplied explicitly — see
[Credentials & stability](#credentials--stability) for where to get them.

```java
NmbsClientConfig config = NmbsClientConfig.builder()
        .rivLegacyApiKey("...")     // live board + RIV train composition
        .rivJourneyApiKey("...")    // journey planner / detail / recon
        .labApiKey("...")           // Lab train composition
        .labBasicAuth("...")        // Lab train composition, base64-encoded "user:pass"
        .userAgent("...")
        .build();

try (NmbsClient client = NmbsClient.create(config)) {

    // Live departure board for Leuven (UIC 8833001)
    LiveBoard<DepartureEntry> board = client.liveBoard()
            .departures("8833001", LocalDateTime.now(), 20);
    board.entries().forEach(d ->
            System.out.println(d.trainNumber() + " " + d.commercialType() + " -> " + d.destinationNl()
                    + " @ " + d.plannedDeparture() + " (delay: " + d.departureDelay() + ")"));

    // Plan a journey between two stations
    JourneyPlanResponse plan = client.journey().plan(
            "8814209", "8833001",              // Nivelles -> Leuven, by UIC code
            LocalDate.now(), LocalTime.now(),
            false,                              // searchForArrival
            6, 0,                               // results after / before the requested time
            true);                              // include the full stopping pattern

    for (Trip trip : plan.trips()) {
        for (Leg leg : trip.legs()) {
            System.out.println(leg.name() + ": " + leg.origin().name() + " -> " + leg.destination().name());

            // Full detail (all stops, live position) of that specific train run
            JourneyDetailResponse detail = client.journey().journeyDetail(leg.journeyDetailRef().ref());
            detail.stops().forEach(s -> System.out.println("  " + s.name()));
        }
    }

    // Rolling-stock composition for a specific train
    TrainComposition composition = client.trainComposition()
            .composition("8814209", "8833605", 4021, LocalDate.now());
    composition.lastPlanned().forEach(segment ->
            segment.materialUnits().forEach(unit ->
                    System.out.println(unit.typeDescriptionNl() + ": " + unit.seatsSecondClass() + " seats (2nd class)")));
}
```

`NmbsClient` holds an HTTP connection pool and is thread-safe; create one instance per application and reuse it,
closing it on shutdown.

### Station identifiers

Stations are identified by their UIC code (e.g. `8833001` for Leuven, `8814209` for Nivelles/Nijvel), the same
codes used throughout the GTFS feed for Belgian rail. This library does not ship a station name/code lookup —
pair it with a GTFS `stops.txt` (UIC code = the last 7 digits of `stop_id`, prefixed with `88`) if you need one.

### Endpoints covered

| Service | Method | Backend |
|---|---|---|
| Live departure board | `client.liveBoard().departures(...)` | `mobile-riv.api.belgianrail.be` |
| Live arrival board | `client.liveBoard().arrivals(...)` | `mobile-riv.api.belgianrail.be` |
| Journey planner | `client.journey().plan(...)` | `mobile-riv.api.belgianrail.be` |
| Journey lookup by train number | `client.journey().planForTrain(...)` | `mobile-riv.api.belgianrail.be` |
| Full train-run detail | `client.journey().journeyDetail(...)` | `mobile-riv.api.belgianrail.be` |
| Re-fetch a planned trip (e.g. with full stops) | `client.journey().reconstructJourney(...)` | `mobile-riv.api.belgianrail.be` |
| Train composition (RIV) | `client.trainComposition().composition(...)` | `mobile-riv.api.belgianrail.be` |
| Train composition (Lab) | `client.trainComposition().labComposition(...)` | `mobile-labapp.api.belgiantrain.be` |

### Error handling

All failures surface as unchecked exceptions rooted at `NmbsApiException`:

- `NmbsApiException.HttpStatus` — the API responded with a non-2xx status (inspect `statusCode()`/`responseBody()`)
- `NmbsApiException.Transport` — the network call itself failed (DNS, connect/read timeout, ...)
- `NmbsApiException.Parsing` — a response body could not be parsed into the expected model

## Credentials & stability

Every API key, the `Authorization: Basic` value, and the `User-Agent` are values the official BelgianTrain app
itself sends on every request. This library does not ship any of them and does not provide defaults — you must
capture your own from the app's traffic (see `contrib/` in this repository for example captures showing the
shape of the requests) and pass them in via `NmbsClientConfig.builder()`, as shown above. `NmbsClientConfig.build()`
throws if any credential or the `User-Agent` is left unset.

NMBS can rotate these values at any time without notice; if requests start failing with `401`/`403`, re-capture
and update them.

`NmbsClientConfig` also lets you override the base URLs and HTTP timeouts, which is how this library's own test
suite points requests at a local fake server instead of the real API.

## Building & testing

```bash
mvn clean verify
```

The unit test suite runs entirely against an in-process fake HTTP server seeded with real response bodies (see
`src/test/resources/fixtures`) — no network access or live credentials are needed to run it. It does not include
tests that call the real API, since that would make CI dependent on a third-party backend that isn't guaranteed
to stay available or stable.

## Disclaimer

This is a community, reverse-engineered client for an internal mobile API, provided as-is with no warranty. Use
of the NMBS/SNCB backend through this library is subject to NMBS/SNCB's own terms; this project claims no
affiliation with them.
