package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;

import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.Utente;

@WebServlet(name = "AutenticazioneController", value = "/AutenticazioneController")

public class AutenticazioneController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println(">>> AUTENTICAZIONE CONTROLLER CHIAMATO <<<");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // controllo parametri minimi
        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {

            response.sendRedirect(request.getContextPath() + "/login.jsp?error=vuoti");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            Utente utente = dao.login(username, password);

            //login corretto
            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);

            System.out.println("LOGIN OK | idUtente=" + utente.getIdUtente()
                    + " | ruolo=" + utente.getRuolo());

            //redirect in base al ruolo
            if ("SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
                response.sendRedirect(request.getContextPath() + "/supervisore/home.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/home.jsp");
            }

        } catch (IllegalArgumentException e) {
            //credenziali errate
            System.out.println("LOGIN FALLITO per username=" + username);
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=credenziali");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=generico");
        }

    }
}

