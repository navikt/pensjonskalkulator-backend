package no.nav.pensjon.kalkulator.mock;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

public abstract class WebClientTest {

    private static MockWebServer server;
    private static String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        baseUrl = String.format("http://localhost:%s", server.getPort());
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    protected static void arrange(MockResponse response) {
        server.enqueue(response);
    }

    protected static RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    protected static MockResponse jsonResponse(HttpStatus status) {
        return jsonResponse()
                .setResponseCode(status.value());
    }

    protected static MockResponse jsonResponse() {
        return new MockResponse()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    protected static String baseUrl() {
        return baseUrl;
    }
}
