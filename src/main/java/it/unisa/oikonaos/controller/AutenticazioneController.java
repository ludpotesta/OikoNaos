package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.CredenzialiDAO;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.URLEncoder;

import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.Utente;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "AutenticazioneController", value = "/AutenticazioneController")
public class AutenticazioneController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null ||
                username.isBlank() || password.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=vuoti");
            return;
        }

        try {
            CredenzialiDAO credDAO = new CredenzialiDAO();

            Long idUtente = credDAO.getIdUtenteByUsername(username);
            if (idUtente == null) {
                throw new IllegalArgumentException();
            }

            String hashDB = credDAO.getPasswordHashByUtente(idUtente);
            if (hashDB == null || !BCrypt.checkpw(password, hashDB)) {
                throw new IllegalArgumentException();
            }

            UserDAO userDAO = new UserDAO();
            Utente utente = userDAO.getUtenteById(idUtente);

            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);

            if ("SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
                response.sendRedirect(request.getContextPath() + "/supervisore/home.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/home.jsp");
            }

        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=credenziali");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=generico");
        }
    }
}
