package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.TokenResetPasswordDAO;
import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.dao.UserDAO;
import util.PasswordValidator;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "ResetPasswordController", value = "/ResetPasswordController")
public class ResetPasswordController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String token = request.getParameter("token");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String conferma = request.getParameter("confermaPassword");

        if (token == null || nuovaPassword == null || conferma == null ||
                nuovaPassword.isBlank() || conferma.isBlank()) {

            response.sendRedirect("resetPassword.jsp?error=campi&token=" + token);
            return;
        }

        if (!nuovaPassword.equals(conferma)) {
            response.sendRedirect("resetPassword.jsp?error=match&token=" + token);
            return;
        }

        try {
            TokenResetPasswordDAO tokenDAO = new TokenResetPasswordDAO();
            Long idUtente = tokenDAO.getIdUtenteByToken(token);

            if (idUtente == null) {
                response.sendRedirect("resetPassword.jsp?error=token");
                return;
            }

            CredenzialiDAO credDAO = new CredenzialiDAO();
            String hash = org.mindrot.jbcrypt.BCrypt.hashpw(
                    nuovaPassword,
                    org.mindrot.jbcrypt.BCrypt.gensalt()
            );

            credDAO.updatePassword(idUtente, hash);
            tokenDAO.invalidateToken(token);

            response.sendRedirect("login.jsp?success=reset");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("resetPassword.jsp?error=generico");
        }
    }
}