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
public class QueryServlet extends HttpServlet {

    private final SQLExecutor executor;

    public QueryServlet(SQLExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            String query = "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1";
            List<Product> productsList = this.executor.executeQuery(query, resultSet -> {
                try {
                    List<Product> products = new ArrayList<>();
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        long price  = resultSet.getLong("price");
                        Product product = new Product(name, price);
                        products.add(product);
                    }
                    return products;
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            String productsView = productsList.stream()
                    .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
                    .collect(Collectors.joining());
            response.getWriter().println(productsView);
            response.getWriter().println("</body></html>");
        } else if ("min".equals(command)) {
            String query = "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1";
            List<Product> productsList = this.executor.executeQuery(query, resultSet -> {
                try {
                    List<Product> products = new ArrayList<>();
                    while (resultSet.next()) {
                        String name = resultSet.getString("name");
                        long price  = resultSet.getLong("price");
                        Product product = new Product(name, price);
                        products.add(product);
                    }
                    return products;
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            String productsView = productsList.stream()
                    .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
                    .collect(Collectors.joining());
            response.getWriter().println(productsView);
            response.getWriter().println("</body></html>");
        } else if ("sum".equals(command)) {
            String query = "SELECT SUM(price) FROM PRODUCT";
            Long sum = this.executor.executeQuery(query, resultSet -> {
                try {
                    if (resultSet.next()) {
                        return resultSet.getLong(1);
                    }
                    return null;
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            });

            response.getWriter().println("<html><body>");
            response.getWriter().println("Summary price: ");
            response.getWriter().println(sum == null ? "" : sum);
            response.getWriter().println("</body></html>");
        } else if ("count".equals(command)) {
            String query = "SELECT COUNT(*) FROM PRODUCT";
            Long count = this.executor.executeQuery(query, resultSet -> {
                try {
                    if (resultSet.next()) {
                        return resultSet.getLong(1);
                    }
                    return null;
                } catch (SQLException e) {
                    throw new RuntimeException(e.getMessage());
                }
            });

            response.getWriter().println("<html><body>");
            response.getWriter().println("Number of products: ");
            response.getWriter().println(count == null ? "" : count);
            response.getWriter().println("</body></html>");
        } else {
            response.getWriter().println("Unknown command: " + command);
        }

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
