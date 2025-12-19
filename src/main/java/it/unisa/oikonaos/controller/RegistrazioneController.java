package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.model.UserDAO;

import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "RegistrazioneController", value = "/RegistrazioneController")

public class RegistrazioneController extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String codice = request.getParameter("codiceID");
        try {

            // Controllo dei dati
            if (nome == null || cognome == null || telefono == null || email == null || password == null || codice == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }

            UserDAO userDAO = new UserDAO();
            userDAO.registerUser(nome, cognome, email, telefono, username, password, codice);
            Utente utente = userDAO.login(username, password);

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