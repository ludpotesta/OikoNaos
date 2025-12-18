package it.unisa.oikonaos.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.database;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TestController", value = "/TestController")
public class TestController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try (Connection con = database.getConnection()) {
            resp.getWriter().println("Connessione OK!");
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().println("Errore DB");
        }
    }

}
