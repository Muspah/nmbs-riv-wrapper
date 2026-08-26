package nl.ovdata.nmbs.internal;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ovdata.nmbs.NmbsApiException;
import nl.ovdata.nmbs.NmbsClientConfig;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thin wrapper around Apache HttpClient5 that performs GET requests with a fixed header set and deserializes JSON
 * responses. Not part of the public API: consumers interact with the per-domain service classes instead.
 */
public final class HttpGateway implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(HttpGateway.class);

    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpGateway(NmbsClientConfig config) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(config.connectTimeout().toMillis()))
                .setResponseTimeout(Timeout.ofMilliseconds(config.responseTimeout().toMillis()))
                .build();
        HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
        this.httpClient = builder.build();
        this.objectMapper = JsonSupport.newMapper();
    }

    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    /**
     * Performs a GET request and deserializes the JSON response body as {@code type}.
     *
     * @param baseUrl scheme+host, e.g. {@code https://mobile-riv.api.belgianrail.be}
     * @param path    request path, e.g. {@code /riv/v2/journey}
     * @param query   query parameters; values are URL-encoded, {@code null} values are omitted
     * @param headers request headers to send as-is
     */
    public <T> T getJson(String baseUrl, String path, Map<String, String> query, Map<String, String> headers,
                         Class<T> type) {
        String body = getRaw(baseUrl, path, query, headers);
        try {
            return objectMapper.readValue(body, type);
        } catch (IOException e) {
            throw new NmbsApiException.Parsing("Failed to parse response from " + path + " as " + type.getSimpleName(), e);
        }
    }

    /** Performs a GET request and deserializes the JSON response body as {@code type}, e.g. for generic types. */
    public <T> T getJson(String baseUrl, String path, Map<String, String> query, Map<String, String> headers,
                         JavaType type) {
        String body = getRaw(baseUrl, path, query, headers);
        try {
            return objectMapper.readValue(body, type);
        } catch (IOException e) {
            throw new NmbsApiException.Parsing("Failed to parse response from " + path + " as " + type, e);
        }
    }

    /** Performs a GET request and returns the raw response body. */
    public String getRaw(String baseUrl, String path, Map<String, String> query, Map<String, String> headers) {
        URI uri = buildUri(baseUrl, path, query);
        HttpGet request = new HttpGet(uri);
        headers.forEach(request::setHeader);

        LOG.debug("GET {}", uri);
        try {
            return httpClient.execute(request, response -> readBody(uri, response));
        } catch (NmbsApiException e) {
            throw e;
        } catch (IOException e) {
            throw new NmbsApiException.Transport("Request to " + uri + " failed", e);
        }
    }

    private String readBody(URI uri, ClassicHttpResponse response) throws IOException {
        String body;
        try {
            body = response.getEntity() == null ? "" : EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } catch (ParseException e) {
            throw new IOException("Failed to read response body from " + uri, e);
        }
        int status = response.getCode();
        if (status < 200 || status >= 300) {
            throw new NmbsApiException.HttpStatus(status, body);
        }
        return body;
    }

    private static URI buildUri(String baseUrl, String path, Map<String, String> query) {
        StringBuilder sb = new StringBuilder(baseUrl).append(path);
        Map<String, String> nonNullQuery = new LinkedHashMap<>();
        query.forEach((key, value) -> {
            if (value != null) {
                nonNullQuery.put(key, value);
            }
        });
        if (!nonNullQuery.isEmpty()) {
            sb.append('?');
            boolean first = true;
            for (Map.Entry<String, String> entry : nonNullQuery.entrySet()) {
                if (!first) {
                    sb.append('&');
                }
                sb.append(urlEncode(entry.getKey())).append('=').append(urlEncode(entry.getValue()));
                first = false;
            }
        }
        try {
            return new URI(sb.toString());
        } catch (URISyntaxException e) {
            throw new NmbsApiException("Built an invalid request URI: " + sb, e);
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    @Override
    public void close() throws IOException {
        httpClient.close();
    }
}
