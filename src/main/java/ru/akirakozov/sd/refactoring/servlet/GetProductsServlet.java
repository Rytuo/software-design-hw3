package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.sql.SQLController;
import ru.akirakozov.sd.refactoring.entity.Product;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    private final SQLController controller;

    public GetProductsServlet(SQLController controller) {
        this.controller = controller;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> productsList = controller.getAllProducts();

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
