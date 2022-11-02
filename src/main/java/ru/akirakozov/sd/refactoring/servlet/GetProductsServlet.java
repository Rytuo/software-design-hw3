package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.Controller;
import ru.akirakozov.sd.refactoring.entity.Product;
import ru.akirakozov.sd.refactoring.view.HtmlTagBuilder;

/**
 * @author akirakozov
 */
public class GetProductsServlet extends HttpServlet {

    private final HtmlTagBuilder tb = new HtmlTagBuilder();
    private final Controller controller;

    public GetProductsServlet(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = controller.getAllProducts();

        String responseContent = tb.document(
                products.stream()
                        .map(product -> product.getName() + "\t" + product.getPrice() + tb.br())
                        .collect(Collectors.joining())
        );
        response.getWriter().println(responseContent);

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
