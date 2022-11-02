package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.Controller;
import ru.akirakozov.sd.refactoring.entity.Product;
import ru.akirakozov.sd.refactoring.view.ResponseBuilder;

/**
 * @author akirakozov
 */
public class QueryServlet extends HttpServlet {

    private final Controller controller;
    private final ResponseBuilder responseBuilder;

    public QueryServlet(Controller controller, ResponseBuilder responseBuilder) {
        this.controller = controller;
        this.responseBuilder = responseBuilder;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        String responseContent;
        if ("max".equals(command)) {
            Product product = controller.getMaxPriceProduct().get(0);
            responseContent = responseBuilder.createMaxTemplate(product);
        } else if ("min".equals(command)) {
            Product product = controller.getMinPriceProduct().get(0);
            responseContent = responseBuilder.createMinTemplate(product);
        } else if ("sum".equals(command)) {
            Long sum = controller.getPriceSum();
            responseContent = responseBuilder.createSumTemplate(sum);
        } else if ("count".equals(command)) {
            Long count = controller.getProductsCount();
            responseContent = responseBuilder.createCountTemplate(count);
        } else {
            responseContent = responseBuilder.createUnknownCommandTemplate(command);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(responseBuilder.getContentType());
        response.getWriter().println(responseContent);
    }

}
