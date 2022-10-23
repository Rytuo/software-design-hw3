package ru.akirakozov.sd.refactoring.servlet;

import java.net.http.HttpClient;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ServletTest {

    static final String DB_URL = "jdbc:sqlite:test.db";

    private static final String INIT_DB = "CREATE TABLE IF NOT EXISTS PRODUCT" +
            "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            " NAME           TEXT    NOT NULL, " +
            " PRICE          INT     NOT NULL)";
    private static final String ADD_PRODUCT = "insert into PRODUCT (NAME, PRICE) values ";
    private static final String GET_PRODUCTS = "select * from PRODUCT";
    private static final String GET_MAX_PRODUCT = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
    private static final String GET_MIN_PRODUCT = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
    private static final String GET_PRODUCTS_PRICE_SUM = "SELECT SUM(price) FROM PRODUCT";
    private static final String GET_PRODUCTS_COUNT = "SELECT COUNT(*) FROM PRODUCT";

    private static final String SERVER_CONTEXT_PATH = "/";
    static final int SERVER_PORT = 8081;

    static final String SERVER_URL = "http://localhost";

    Server server;
    HttpClient client;

    abstract void addServlet(ServletContextHandler contextHandler);

    @BeforeAll
    void beforeAll() throws Exception {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(INIT_DB);
            stmt.close();
        }

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

    void addProduct(String name, long price) throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = c.createStatement()) {
                stmt.executeUpdate(ADD_PRODUCT + String.format("(\"%s\", %s)", name, price));
            }
        }
    }

    Map<String, Long> getProducts() throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = c.createStatement()) {
                Map<String, Long> products = new HashMap<>();
                ResultSet resultSet = stmt.executeQuery(GET_PRODUCTS);
                while (resultSet.next()) {
                    products.put(resultSet.getString("name"),
                            resultSet.getLong("price"));
                }
                return products;
            }
        }
    }

    Map<String, Long> getMaxProduct() throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = c.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(GET_MAX_PRODUCT);
                if (resultSet.next()) {
                    return Map.of(resultSet.getString("name"),
                            resultSet.getLong("price"));
                }
                return Collections.emptyMap();
            }
        }
    }

    Map<String, Long> getMinProduct() throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = c.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(GET_MIN_PRODUCT);
                if (resultSet.next()) {
                    return Map.of(resultSet.getString("name"),
                            resultSet.getLong("price"));
                }
                return Collections.emptyMap();
            }
        }
    }

    Long getProductsSum() throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = c.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(GET_PRODUCTS_PRICE_SUM);
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return -1L;
            }
        }
    }

    Long getProductsCount() throws SQLException {
        try (Connection c = DriverManager.getConnection(DB_URL)) {
            try (Statement stmt = c.createStatement()) {
                ResultSet resultSet = stmt.executeQuery(GET_PRODUCTS_COUNT);
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return -1L;
            }
        }
    }
}
