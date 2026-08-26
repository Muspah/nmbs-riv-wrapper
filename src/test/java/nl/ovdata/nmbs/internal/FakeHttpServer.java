package nl.ovdata.nmbs.internal;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Minimal single-threaded HTTP server (backed by the JDK's built-in {@code com.sun.net.httpserver}) used to test
 * this library's request-building and response-parsing without any external dependency or real network call.
 */
public final class FakeHttpServer implements AutoCloseable {

    /** Snapshot of a single request the server received. */
    public record RecordedRequest(String method, URI uri, Map<String, String> headers) {
        public String header(String name) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(name)) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }

    private final HttpServer server;
    private final Deque<RecordedRequest> requests = new ArrayDeque<>();
    private String nextResponseBody = "";
    private int nextResponseStatus = 200;

    public FakeHttpServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
        server.createContext("/", exchange -> handle(exchange));
        server.start();
    }

    public String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    /** Sets the body and status the next handled request will be answered with. */
    public void respondWith(int status, String body) {
        this.nextResponseStatus = status;
        this.nextResponseBody = body;
    }

    public RecordedRequest lastRequest() {
        return requests.peekLast();
    }

    private void handle(HttpExchange exchange) {
        try {
            Map<String, String> headers = new LinkedHashMap<>();
            exchange.getRequestHeaders().forEach((key, values) -> headers.put(key, String.join(",", values)));
            requests.addLast(new RecordedRequest(exchange.getRequestMethod(), exchange.getRequestURI(),
                    Collections.unmodifiableMap(headers)));

            byte[] payload = nextResponseBody.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.sendResponseHeaders(nextResponseStatus, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private static final class UncheckedException extends RuntimeException {
        UncheckedException(Throwable cause) {
            super(cause);
        }
    }
}
