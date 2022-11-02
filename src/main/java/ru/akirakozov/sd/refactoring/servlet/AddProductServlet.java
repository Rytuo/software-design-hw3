package ru.akirakozov.sd.refactoring.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.akirakozov.sd.refactoring.controller.Controller;
import ru.akirakozov.sd.refactoring.entity.Product;

/**
 * @author akirakozov
 */
public class AddProductServlet extends HttpServlet {

    private final Controller controller;

    public AddProductServlet(Controller controller) {
        this.controller = controller;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Product product = parseRequest(request);

        controller.addProducts(List.of(product));

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("OK");
    }

    private Product parseRequest(HttpServletRequest request) {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));
        return new Product(name, price);
    }
}
