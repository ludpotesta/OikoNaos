package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "RegistrazioneController", value = "/RegistrazioneController")
public class RegistrazioneController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String codice = request.getParameter("codiceID");

        // Validazione input base
        if (nome == null || cognome == null || email == null ||
                telefono == null || username == null ||
                password == null || codice == null ||
                nome.isBlank() || cognome.isBlank() ||
                username.isBlank() || password.isBlank() ||
                codice.isBlank()) {

            response.sendRedirect(request.getContextPath() + "/register.jsp?error=campi");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            // Registrazione
            userDAO.registerUser(nome, cognome, email, telefono, username, password, codice);

            // Login automatico
            Utente utente = userDAO.login(username, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);

            System.out.println("Utente registrato e loggato: " + utente.getNome());

            // Redirect post-registrazione
            response.sendRedirect(request.getContextPath() + "/home.jsp");

        } catch (Exception e) {
            e.printStackTrace();

            String errorMessage = URLEncoder.encode(e.getMessage(), "UTF-8");
            response.sendRedirect(
                    request.getContextPath() + "/register.jsp?error=" + errorMessage
            );
        }
    }
}
