package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.sql.SQLExecutor;
import ru.akirakozov.sd.refactoring.controller.sql.SQLQueries;
import ru.akirakozov.sd.refactoring.controller.sql.SQLResultCollector;
import ru.akirakozov.sd.refactoring.entity.Product;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

    private final SQLExecutor executor;
    private final SQLResultCollector collector;

    public QueryServlet(SQLExecutor executor, SQLResultCollector collector) {
        this.executor = executor;
        this.collector = collector;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            List<Product> products = this.executor.executeQuery(SQLQueries.GET_MAX_PRICE_PRODUCT.getQuery(), collector::collectProducts);
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            String productsView = products.stream()
                    .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
                    .collect(Collectors.joining());
            response.getWriter().println(productsView);
            response.getWriter().println("</body></html>");
        } else if ("min".equals(command)) {
            List<Product> products = this.executor.executeQuery(SQLQueries.GET_MIN_PRICE_PRODUCT.getQuery(), collector::collectProducts);
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            String productsView = products.stream()
                    .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
                    .collect(Collectors.joining());
            response.getWriter().println(productsView);
            response.getWriter().println("</body></html>");
        } else if ("sum".equals(command)) {
            Long sum = this.executor.executeQuery(SQLQueries.GET_PRICE_SUM.getQuery(), collector::collectLong);

            response.getWriter().println("<html><body>");
            response.getWriter().println("Summary price: ");
            response.getWriter().println(sum == null ? "" : sum);
            response.getWriter().println("</body></html>");
        } else if ("count".equals(command)) {
            Long count = this.executor.executeQuery(SQLQueries.GET_PRODUCTS_COUNT.getQuery(), collector::collectLong);

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
