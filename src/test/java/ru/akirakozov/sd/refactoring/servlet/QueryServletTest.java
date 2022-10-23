package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import ru.akirakozov.sd.refactoring.entity.Product;

public class QueryServletTest extends ServletTest {

    private static final String SERVER_QUERY_COMMAND_PATH = "/query";
    private static final String MAX_COMMAND = "?command=max";
    private static final String MIN_COMMAND = "?command=min";
    private static final String SUM_COMMAND = "?command=sum";
    private static final String COUNT_COMMAND = "?command=count";

    @Override
    void addServlet(ServletContextHandler contextHandler) {
        contextHandler.addServlet(new ServletHolder(new QueryServlet()), SERVER_QUERY_COMMAND_PATH);
    }

    @Test
    void testMax() throws SQLException, IOException, InterruptedException {
        String name1 = "test1" + System.currentTimeMillis();
        long price1 = System.currentTimeMillis();
        addProduct(name1, price1);

        String uri = SERVER_URL + ":" + SERVER_PORT + SERVER_QUERY_COMMAND_PATH + MAX_COMMAND;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpServletResponse.SC_OK);
        String contentType = response.headers().firstValue("content-type").orElse(null);
        assertThat(contentType).contains("text/html");
        Product product = getMaxProduct();
        assertThat(response.body()).contains(product.getName() + "\t" + product.getPrice());
    }

    @Test
    void testMin() throws SQLException, IOException, InterruptedException {
        String name1 = "test1" + System.currentTimeMillis();
        long price1 = System.currentTimeMillis();
        addProduct(name1, price1);

        String uri = SERVER_URL + ":" + SERVER_PORT + SERVER_QUERY_COMMAND_PATH + MIN_COMMAND;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpServletResponse.SC_OK);
        String contentType = response.headers().firstValue("content-type").orElse(null);
        assertThat(contentType).contains("text/html");
        Product product = getMinProduct();
        assertThat(response.body()).contains(product.getName() + "\t" + product.getPrice());
    }

    @Test
    void testSum() throws IOException, InterruptedException, SQLException {
        String uri = SERVER_URL + ":" + SERVER_PORT + SERVER_QUERY_COMMAND_PATH + SUM_COMMAND;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpServletResponse.SC_OK);
        String contentType = response.headers().firstValue("content-type").orElse(null);
        assertThat(contentType).contains("text/html");
        assertThat(response.body()).contains(getProductsSum().toString());
    }

    @Test
    void testCount() throws IOException, InterruptedException, SQLException {
        String uri = SERVER_URL + ":" + SERVER_PORT + SERVER_QUERY_COMMAND_PATH + COUNT_COMMAND;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpServletResponse.SC_OK);
        String contentType = response.headers().firstValue("content-type").orElse(null);
        assertThat(contentType).contains("text/html");
        assertThat(response.body()).contains(getProductsCount().toString());
    }

    @Test
    void testInvalidCommand() throws IOException, InterruptedException {
        String uri = SERVER_URL + ":" + SERVER_PORT + SERVER_QUERY_COMMAND_PATH + "?command=invalid";
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpServletResponse.SC_OK);
        String contentType = response.headers().firstValue("content-type").orElse(null);
        assertThat(contentType).contains("text/html");
        assertThat(response.body()).contains("Unknown command: invalid");
    }
}
