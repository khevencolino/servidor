

import com.kheven.config.Config;
import com.kheven.http.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTest {
    private HttpServer server;
    private ExecutorService executor;

    @BeforeEach
    public void setUp() {
        server = new HttpServer();
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @AfterEach
    public void tearDown() {
        executor.shutdownNow();
    }

    @Test
    public void testGetRequest() throws IOException {
        URL url = new URL("http://localhost:" + Config.getPort() + "/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
    }

    @Test
    public void testApiRequest() throws IOException {
        URL url = new URL("http://localhost:" + Config.getPort() + "/api/users");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(200, responseCode);
        assertEquals("application/json", connection.getContentType());
    }

    @Test
    public void testNotFoundRequest() throws IOException {
        URL url = new URL("http://localhost:" + Config.getPort() + "/nonexistent");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        assertEquals(404, responseCode);
    }
}