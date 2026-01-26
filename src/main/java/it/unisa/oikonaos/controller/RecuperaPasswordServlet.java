package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.utils.EmailSender;
import it.unisa.oikonaos.utils.PasswordUtil;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "RecuperaPasswordServlet", value = "/recupera-password")
public class RecuperaPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        try {
            UserDAO userDAO = new UserDAO();
            Utente utente = userDAO.doRetrieveByEmail(email);

            if (utente != null) {
                String tempPassword = UUID.randomUUID().toString().substring(0, 8);
                String hashed = PasswordUtil.hashPassword(tempPassword);

                boolean aggiornata = userDAO.aggiornaPasswordByEmail(email, hashed);

                if (aggiornata) {
                    new Thread(() -> EmailSender.inviaEmail(email, "Recupero Password",
                            "La tua nuova password temporanea è: " + tempPassword)).start();

                    HttpSession session = request.getSession();
                    utente.setPassword(hashed);
                    session.setAttribute("utente", utente);

                    response.sendRedirect(request.getContextPath() + "/views/modifica-password.jsp?recupero=true");
                    return;
                } else {
                    request.setAttribute("errore", "Errore durante l'aggiornamento della password");
                }
            } else {
                request.setAttribute("errore", "Email non trovata");
            }

            request.getRequestDispatcher("/views/recupera-password.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback error handling
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=generico");
        }
    }
}
