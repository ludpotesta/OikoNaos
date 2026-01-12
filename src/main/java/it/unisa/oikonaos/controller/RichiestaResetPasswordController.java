package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.TokenResetPasswordDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "RichiestaResetPasswordController",  value = "/RichiestaResetPasswordController")
public class RichiestaResetPasswordController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");

        if (email == null || email.isBlank()) {
            response.sendRedirect("passwordDimenticata.jsp?error=campi");
            return;
        }

        try {
            CredenzialiDAO credDAO = new CredenzialiDAO();
            Long idUtente = credDAO.getIdUtenteByEmail(email);

            // Security: non riveliamo se esiste
            if (idUtente != null) {
                TokenResetPasswordDAO tokenDAO = new TokenResetPasswordDAO();
                String token = tokenDAO.createToken(idUtente);

                String link = request.getContextPath()
                        + "/resetPassword.jsp?token=" + URLEncoder.encode(token, "UTF-8");

                response.sendRedirect(
                        "passwordDimenticata.jsp?success=ok&link=" +
                                URLEncoder.encode(link, "UTF-8")
                );
                return;
            }

            response.sendRedirect("passwordDimenticata.jsp?success=ok");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("passwordDimenticata.jsp?error=generico");
        }
    }
}
