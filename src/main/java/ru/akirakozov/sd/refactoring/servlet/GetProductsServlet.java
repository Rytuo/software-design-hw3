package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.sql.SQLExecutor;
import ru.akirakozov.sd.refactoring.controller.sql.SQLResultCollector;
import ru.akirakozov.sd.refactoring.entity.Product;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    private final SQLExecutor executor;
    private final SQLResultCollector collector;

    public GetProductsServlet(SQLExecutor executor, SQLResultCollector collector) {
        this.executor = executor;
        this.collector = collector;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String query = "SELECT * FROM PRODUCT";
        List<Product> productsList = this.executor.executeQuery(query, collector::collectProducts);

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
