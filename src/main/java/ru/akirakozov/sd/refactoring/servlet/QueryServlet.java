package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.Controller;
import ru.akirakozov.sd.refactoring.entity.Product;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

    private final Controller controller;

    public QueryServlet(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if ("max".equals(command)) {
            List<Product> products = controller.getMaxPriceProduct();
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            String productsView = products.stream()
                    .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
                    .collect(Collectors.joining());
            response.getWriter().println(productsView);
            response.getWriter().println("</body></html>");
        } else if ("min".equals(command)) {
            List<Product> products = controller.getMinPriceProduct();
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h1>Product with max price: </h1>");
            String productsView = products.stream()
                    .map(product -> product.getName() + "\t" + product.getPrice() + "</br>")
                    .collect(Collectors.joining());
            response.getWriter().println(productsView);
            response.getWriter().println("</body></html>");
        } else if ("sum".equals(command)) {
            Long sum = controller.getPriceSum();

            response.getWriter().println("<html><body>");
            response.getWriter().println("Summary price: ");
            response.getWriter().println(sum == null ? "" : sum);
            response.getWriter().println("</body></html>");
        } else if ("count".equals(command)) {
            Long count = controller.getProductsCount();

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
