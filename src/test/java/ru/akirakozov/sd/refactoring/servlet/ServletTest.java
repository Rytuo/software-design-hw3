package ru.akirakozov.sd.refactoring.servlet;

import java.net.http.HttpClient;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import ru.akirakozov.sd.refactoring.controller.sql.SQLExecutor;
import ru.akirakozov.sd.refactoring.controller.sql.SQLQueries;
import ru.akirakozov.sd.refactoring.controller.sql.SQLResultCollector;
import ru.akirakozov.sd.refactoring.entity.Product;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ServletTest {

    static final String DB_URL = "jdbc:sqlite:test.db";

    private static final String SERVER_CONTEXT_PATH = "/";
    static final int SERVER_PORT = 8081;

    static final String SERVER_URL = "http://localhost";

    SQLExecutor executor;
    SQLResultCollector collector;
    Server server;
    HttpClient client;

    abstract void addServlet(ServletContextHandler contextHandler);

    @BeforeAll
    void beforeAll() throws Exception {
        executor = new SQLExecutor(DB_URL);
        executor.executeUpdate(SQLQueries.INIT.getQuery());

        collector = new SQLResultCollector();

        this.server = new Server(SERVER_PORT);
        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.setContextPath(SERVER_CONTEXT_PATH);
        addServlet(contextHandler);
        server.setHandler(contextHandler);
        server.start();

        client = HttpClient.newBuilder().build();
    }

    @AfterAll
    void afterAll() throws Exception {
        server.stop();
    }

    void addProduct(String name, long price) {
        executor.executeUpdate(String.format(SQLQueries.ADD_PRODUCTS.getQuery(), "(\"" + name + "\", " + price + ")"));
    }

    List<Product> getProducts() {
        return executor.executeQuery(SQLQueries.GET_ALL_PRODUCTS.getQuery(), collector::collectProducts);
    }

    Product getMaxProduct() {
        return executor.executeQuery(SQLQueries.GET_MAX_PRICE_PRODUCT.getQuery(), collector::collectProduct);
    }

    Product getMinProduct() {
        return executor.executeQuery(SQLQueries.GET_MIN_PRICE_PRODUCT.getQuery(), collector::collectProduct);
    }

    Long getProductsSum() {
        return executor.executeQuery(SQLQueries.GET_PRICE_SUM.getQuery(), collector::collectLong);
    }

    Long getProductsCount() {
        return executor.executeQuery(SQLQueries.GET_PRODUCTS_COUNT.getQuery(), collector::collectLong);
    }
}
