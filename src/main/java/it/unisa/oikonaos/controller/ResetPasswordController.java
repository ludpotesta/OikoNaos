package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.TokenResetPasswordDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

@WebServlet(name = "ResetPasswordController", value = "/ResetPasswordController")
public class ResetPasswordController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("resetToken") == null) {
            response.sendRedirect(request.getContextPath() + "/resetPassword.jsp?error=token");
            return;
        }

        String token = (String) session.getAttribute("resetToken");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String conferma = request.getParameter("confermaPassword");

        if (nuovaPassword == null || conferma == null ||
                nuovaPassword.isBlank() || conferma.isBlank()) {

            response.sendRedirect(request.getContextPath() + "/resetPassword.jsp?error=campi");
            return;
        }

        if (!nuovaPassword.equals(conferma)) {
            response.sendRedirect(request.getContextPath() + "/resetPassword.jsp?error=match");
            return;
        }

        try {
            TokenResetPasswordDAO tokenDAO = new TokenResetPasswordDAO();
            Long idUtente = tokenDAO.getIdUtenteByToken(token);

            if (idUtente == null) {
                response.sendRedirect(request.getContextPath() + "/resetPassword.jsp?error=token");
                return;
            }

            String hash = BCrypt.hashpw(nuovaPassword, BCrypt.gensalt());
            new CredenzialiDAO().updatePassword(idUtente, hash);

            tokenDAO.invalidateToken(token);
            session.invalidate(); // 🔥 IMPORTANTISSIMO

            response.sendRedirect(request.getContextPath() + "/login.jsp?success=reset");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/resetPassword.jsp?error=generico");
        }
    }
}
