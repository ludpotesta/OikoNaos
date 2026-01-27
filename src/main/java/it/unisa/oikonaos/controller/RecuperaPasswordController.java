package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.dao.UserDAO;
import util.EmailSender;
import util.PasswordUtil;

import java.io.IOException;
import java.util.UUID;

@WebServlet(name = "RecuperaPasswordServlet", value = "/recupera-password")
public class RecuperaPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        try {
            UserDAO userDAO = new UserDAO();
            Utente utente = userDAO.doRetrieveByEmail(email);

            if (utente != null) {
                // Test hook (solo in locale): permette ai test Selenium di forzare una password temporanea.
                boolean isLocal = request.getRemoteAddr() != null && (
                        request.getRemoteAddr().equals("127.0.0.1") ||
                        request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ||
                        request.getRemoteAddr().equals("::1")
                );

                boolean testMode = isLocal && "true".equalsIgnoreCase(request.getParameter("testMode"));

                String tempPassword;
                if (testMode) {
                    String forced = request.getParameter("forcedTemp");
                    tempPassword = (forced != null && !forced.isBlank()) ? forced : "Ciao1205!";
                } else {
                    String forcedSys = System.getProperty("oikonaos.tempPassword");
                    tempPassword = (forcedSys != null && !forcedSys.isBlank())
                            ? forcedSys
                            : UUID.randomUUID().toString().substring(0, 8);
                }

                String hashed = PasswordUtil.hashPassword(tempPassword);

                boolean aggiornata = userDAO.aggiornaPasswordByEmail(email, hashed);

                if (aggiornata) {
                    if (!testMode) {
                        new Thread(() -> EmailSender.inviaEmail(email, "Recupero Password",
                                "La tua nuova password temporanea è: " + tempPassword)).start();
                    }

                    HttpSession session = request.getSession();
                    utente.setPassword(hashed);
                    session.setAttribute("utente", utente);

                    response.sendRedirect(request.getContextPath() + "/modifica-password.jsp?recupero=true");
                    return;
                } else {
                    request.setAttribute("errore", "Errore durante l'aggiornamento della password");
                }
            } else {
                request.setAttribute("errore", "Email non trovata");
            }

            request.getRequestDispatcher("/recupera-password.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback error handling
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=generico");
        }
    }
}
