package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.TokenResetPasswordDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "RichiestaResetPasswordController", value = "/RichiestaResetPasswordController")
public class RichiestaResetPasswordController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/passwordDimenticata.jsp?error=campi");
            return;
        }

        try {
            CredenzialiDAO credDAO = new CredenzialiDAO();
            Long idUtente = credDAO.getIdUtenteByEmail(email);

            // Non riveliamo se l’email esiste
            if (idUtente != null) {
                TokenResetPasswordDAO tokenDAO = new TokenResetPasswordDAO();
                String token = tokenDAO.createToken(idUtente);

                //SALVIAMO IL TOKEN IN SESSIONE
                HttpSession session = request.getSession(true);
                session.setAttribute("resetToken", token);
            }

            response.sendRedirect(request.getContextPath() + "/resetPassword.jsp");


        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/passwordDimenticata.jsp?error=generico");
        }
    }
}
