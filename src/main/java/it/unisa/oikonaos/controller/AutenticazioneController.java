package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;

import it.unisa.oikonaos.model.UserDAO;
import it.unisa.oikonaos.model.Utente;

@WebServlet(name = "AutenticazioneController", value = "/AutenticazioneController")

public class AutenticazioneController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">>> AUTENTICAZIONE CONTROLLER CHIAMATO <<<");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            UserDAO dao = new UserDAO();
            Utente utente = dao.login(username, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);

            System.out.println("Utente in sessione: " + utente.getNome());

            response.sendRedirect(request.getContextPath() + "/home.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = URLEncoder.encode(e.getMessage(), "UTF-8");
        }
    }
}

