package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import ru.akirakozov.sd.refactoring.controller.Controller;
import ru.akirakozov.sd.refactoring.controller.sql.SQLController;
import ru.akirakozov.sd.refactoring.controller.sql.SQLExecutor;
import ru.akirakozov.sd.refactoring.controller.sql.SQLResultCollector;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;
import ru.akirakozov.sd.refactoring.view.HtmlResponseBuilder;
import ru.akirakozov.sd.refactoring.view.ResponseBuilder;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Controller controller = new SQLController(
                new SQLExecutor("jdbc:sqlite:prod.db"),
                new SQLResultCollector()
        );
        ResponseBuilder responseBuilder = new HtmlResponseBuilder();

        controller.init();

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(controller, responseBuilder)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(controller, responseBuilder)),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(controller, responseBuilder)),"/query");

        server.start();
        server.join();
    }
}
