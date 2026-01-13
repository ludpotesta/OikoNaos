package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.model.Utente;
import util.PasswordValidator;

import java.net.URLEncoder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "ModificaCredenzialiController", value = "/ModificaCredenzialiController")
public class ModificaCredenzialiController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            CredenzialiDAO dao = new CredenzialiDAO();

            String nuovoUsername = request.getParameter("nuovoUsername");
            String passwordAttuale = request.getParameter("passwordAttuale");
            String nuovaPassword = request.getParameter("nuovaPassword");
            String conferma = request.getParameter("confermaPassword");

            // verifica password attuale
            String hashAttuale = dao.getPasswordHashByUtente(utente.getIdUtente());
            if (!BCrypt.checkpw(passwordAttuale, hashAttuale)) {
                response.sendRedirect("home.jsp?error=password_errata");
                return;
            }

            // cambio username
            if (nuovoUsername != null && !nuovoUsername.isBlank()) {
                if (dao.usernameEsistente(nuovoUsername)) {
                    response.sendRedirect("home.jsp?error=username_usato");
                    return;
                }
                dao.updateUsername(utente.getIdUtente(), nuovoUsername);
            }

            // cambio password
            if (nuovaPassword != null && !nuovaPassword.isBlank()) {

                if (!nuovaPassword.equals(conferma)) {
                    response.sendRedirect("home.jsp?error=password_match");
                    return;
                }

                String esito = PasswordValidator.validate(
                        nuovaPassword,
                        utente.getNome(),
                        utente.getCognome(),
                        null
                );

                if (esito != null) {
                    response.sendRedirect("home.jsp?error=password_" + esito);
                    return;
                }

                String nuovoHash = BCrypt.hashpw(nuovaPassword, BCrypt.gensalt());
                dao.updatePassword(utente.getIdUtente(), nuovoHash);
            }

            response.sendRedirect("home.jsp?success=credenziali");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home.jsp?error=generico");
        }
    }
}
