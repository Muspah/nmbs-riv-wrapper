package nl.ovdata.nmbs;

/**
 * Root exception for all failures raised by this client: network failures, non-2xx HTTP responses,
 * and response bodies that cannot be parsed.
 */
public class NmbsApiException extends RuntimeException {

    public NmbsApiException(String message) {
        super(message);
    }

    public NmbsApiException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Raised when the API responds with a non-2xx HTTP status code.
     */
    public static final class HttpStatus extends NmbsApiException {

        private final int statusCode;
        private final String responseBody;

        public HttpStatus(int statusCode, String responseBody) {
            super("NMBS API responded with HTTP " + statusCode
                    + (responseBody == null || responseBody.isBlank() ? "" : ": " + truncate(responseBody)));
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public int statusCode() {
            return statusCode;
        }

        public String responseBody() {
            return responseBody;
        }

        private static String truncate(String body) {
            return body.length() > 500 ? body.substring(0, 500) + "..." : body;
        }
    }

    /**
     * Raised when the network call itself fails (DNS, connect/read timeout, TLS, ...).
     */
    public static final class Transport extends NmbsApiException {
        public Transport(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Raised when a response body could not be parsed into the expected model.
     */
    public static final class Parsing extends NmbsApiException {
        public Parsing(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
