package ru.akirakozov.sd.refactoring;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import ru.akirakozov.sd.refactoring.controller.sql.SQLExecutor;
import ru.akirakozov.sd.refactoring.controller.sql.SQLResultCollector;
import ru.akirakozov.sd.refactoring.servlet.AddProductServlet;
import ru.akirakozov.sd.refactoring.servlet.GetProductsServlet;
import ru.akirakozov.sd.refactoring.servlet.QueryServlet;

/**
 * @author akirakozov
 */
public class Main {
    public static void main(String[] args) throws Exception {
        SQLExecutor executor = new SQLExecutor("jdbc:sqlite:test.db");
        SQLResultCollector collector = new SQLResultCollector();

        String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " PRICE          INT     NOT NULL)";
        executor.executeUpdate(sql);

        Server server = new Server(8081);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(executor)), "/add-product");
        context.addServlet(new ServletHolder(new GetProductsServlet(executor, collector)),"/get-products");
        context.addServlet(new ServletHolder(new QueryServlet(executor, collector)),"/query");

        server.start();
        server.join();
    }
}
