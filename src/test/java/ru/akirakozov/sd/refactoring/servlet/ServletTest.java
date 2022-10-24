package ru.akirakozov.sd.refactoring.servlet;

import java.net.http.HttpClient;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import ru.akirakozov.sd.refactoring.controller.sql.SQLExecutor;
import ru.akirakozov.sd.refactoring.entity.Product;

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

    SQLExecutor executor;
    Server server;
    HttpClient client;

    abstract void addServlet(ServletContextHandler contextHandler);

    @BeforeAll
    void beforeAll() throws Exception {
        executor = new SQLExecutor(DB_URL);
        executor.executeUpdate(INIT_DB);

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
        executor.executeUpdate(ADD_PRODUCT + String.format("(\"%s\", %s)", name, price));
    }

    List<Product> getProducts() {
        return executor.executeQuery(GET_PRODUCTS, resultSet -> {
            try {
                List<Product> products = new ArrayList<>();
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    long price = resultSet.getLong("price");
                    products.add(new Product(name, price));
                }
                return products;
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    Product getMaxProduct() {
        return executor.executeQuery(GET_MAX_PRODUCT, resultSet -> {
            try {
                if (resultSet.next()) {
                    return new Product(
                            resultSet.getString("name"),
                            resultSet.getLong("price")
                    );
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    Product getMinProduct() {
        return executor.executeQuery(GET_MIN_PRODUCT, resultSet -> {
            try {
                if (resultSet.next()) {
                    return new Product(
                            resultSet.getString("name"),
                            resultSet.getLong("price")
                    );
                }
                return null;
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    Long getProductsSum() {
        return executor.executeQuery(GET_PRODUCTS_PRICE_SUM, resultSet -> {
            try {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return -1L;
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    Long getProductsCount() {
        return executor.executeQuery(GET_PRODUCTS_COUNT, resultSet -> {
            try {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                return -1L;
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }
}
