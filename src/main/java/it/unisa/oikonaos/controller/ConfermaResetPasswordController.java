package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.TokenResetPasswordDAO;
import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.dao.UserDAO;
import util.PasswordValidator;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "ConfermaResetPasswordController", value = "/ConfermaResetPasswordController")
public class ConfermaResetPasswordController extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String token = request.getParameter("token");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String conferma = request.getParameter("confermaPassword");

        if (token == null || nuovaPassword == null || conferma == null ||
                token.isBlank() || nuovaPassword.isBlank() || conferma.isBlank()) {

            response.sendRedirect(
                    request.getContextPath() + "/resetPassword.jsp?error=campi"
            );
            return;
        }

        if (!nuovaPassword.equals(conferma)) {
            response.sendRedirect(
                    request.getContextPath() + "/resetPassword.jsp?error=match&token="
                            + URLEncoder.encode(token, StandardCharsets.UTF_8)
            );
            return;
        }

        try {
            TokenResetPasswordDAO tokenDAO = new TokenResetPasswordDAO();
            Long idUtente = tokenDAO.getIdUtenteByToken(token);

            // token non valido o scaduto
            if (idUtente == null) {
                response.sendRedirect(
                        request.getContextPath() + "/resetPassword.jsp?error=token"
                );
                return;
            }

            UserDAO userDAO = new UserDAO();
            Utente u = userDAO.getUtenteById(idUtente);

            if (u == null) {
                response.sendRedirect(
                        request.getContextPath() + "/resetPassword.jsp?error=token"
                );
                return;
            }

            // Validazione password (RAD compliant)
            String esito = PasswordValidator.validate(
                    nuovaPassword,
                    u.getNome(),
                    u.getCognome(),
                    null
            );

            if (esito != null) {
                response.sendRedirect(
                        request.getContextPath()
                                + "/resetPassword.jsp?error=pwd&msg="
                                + URLEncoder.encode(esito, StandardCharsets.UTF_8)
                                + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)
                );
                return;
            }

            // Aggiornamento password
            String hash = BCrypt.hashpw(nuovaPassword, BCrypt.gensalt());

            CredenzialiDAO credDAO = new CredenzialiDAO();
            credDAO.updatePassword(idUtente, hash);

            // Token one-time use
            tokenDAO.invalidateToken(token);

            // Redirect finale
            response.sendRedirect(
                    request.getContextPath() + "/login.jsp?success=reset"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/resetPassword.jsp?error=generico"
            );
        }
    }
}
