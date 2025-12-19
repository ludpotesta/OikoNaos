package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "LogoutController", value = "/LogoutController")
public class LogoutController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[LOGOUT] Richiesta logout");

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
            System.out.println("[LOGOUT] Sessione invalidata");
        }

        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }
}
