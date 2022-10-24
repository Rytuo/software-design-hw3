package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.sql.SQLExecutor;
import ru.akirakozov.sd.refactoring.entity.Product;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    private final SQLExecutor executor;

    public GetProductsServlet(SQLExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String query = "SELECT * FROM PRODUCT";
        List<Product> productsList = this.executor.executeQuery(query, resultSet -> {
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

        response.getWriter().println("<html><body>");
        String productsView = productsList.stream()
                .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
                .collect(Collectors.joining());
        response.getWriter().println(productsView);
        response.getWriter().println("</body></html>");

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
