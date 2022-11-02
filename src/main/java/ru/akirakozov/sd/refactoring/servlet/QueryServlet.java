package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.Controller;
import ru.akirakozov.sd.refactoring.entity.Product;
import ru.akirakozov.sd.refactoring.view.HtmlTagBuilder;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

    private final HtmlTagBuilder tb = new HtmlTagBuilder();
    private final Controller controller;

    public QueryServlet(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        String responseContent;
        if ("max".equals(command)) {
            Product product = controller.getMaxPriceProduct().get(0);
            responseContent = tb.document(
                    tb.h1("Product with max price: ") +
                            product.getName() + "\t" + product.getPrice() + tb.br()
            );
        } else if ("min".equals(command)) {
            Product product = controller.getMinPriceProduct().get(0);
            responseContent = tb.document(
                    tb.h1("Product with min price: ") +
                            product.getName() + "\t" + product.getPrice() + tb.br()
            );
        } else if ("sum".equals(command)) {
            Long sum = controller.getPriceSum();
            responseContent = tb.document("Summary price: " + (sum == null ? "" : sum));
        } else if ("count".equals(command)) {
            Long count = controller.getProductsCount();
            responseContent = tb.document("Number of products: " + (count == null ? "" : count));
        } else {
            responseContent = "Unknown command: " + command;
        }
        response.getWriter().println(responseContent);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
