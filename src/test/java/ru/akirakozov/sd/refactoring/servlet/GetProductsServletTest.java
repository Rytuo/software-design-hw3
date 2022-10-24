package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetProductsServletTest extends ServletTest {

    private static final String SERVER_GET_PRODUCT_PATH = "/get-products";

    @Override
    void addServlet(ServletContextHandler contextHandler) {
        contextHandler.addServlet(new ServletHolder(new GetProductsServlet(executor)), SERVER_GET_PRODUCT_PATH);
    }

    @Test
    void testGetProducts() throws IOException, InterruptedException {
        String name = "test1" + System.currentTimeMillis();
        long price = System.currentTimeMillis();
        addProduct(name, price);

        String uri = SERVER_URL + ":" + SERVER_PORT + SERVER_GET_PRODUCT_PATH;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(HttpServletResponse.SC_OK);
        String contentType = response.headers().firstValue("content-type").orElse(null);
        assertThat(contentType).contains("text/html");
        assertThat(response.body()).contains(name + "\t" + price);
    }
}
