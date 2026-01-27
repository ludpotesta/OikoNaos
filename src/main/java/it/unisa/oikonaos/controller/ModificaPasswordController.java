package it.unisa.oikonaos.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.dao.UserDAO;
import util.PasswordUtil;

import java.io.IOException;

@WebServlet(name = "ModificaPasswordController", value = "/modifica-password")
public class ModificaPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");

        boolean recupero = request.getParameter("recupero") != null;

        String vecchiaPassword = request.getParameter("vecchiaPassword");
        String nuovaPassword = request.getParameter("nuovaPassword");
        String confermaPassword = request.getParameter("confermaPassword");

        if (vecchiaPassword == null || nuovaPassword == null || confermaPassword == null) {
            request.setAttribute("errore", "Tutti i campi sono obbligatori.");
            request.getRequestDispatcher("/modifica-password.jsp").forward(request, response);
            return;
        }

        if (!nuovaPassword.equals(confermaPassword)) {
            request.setAttribute("errore", "Le nuove password non coincidono");
            request.getRequestDispatcher("/modifica-password.jsp").forward(request, response);
            return;
        }

        if (!recupero) {
            if (!PasswordUtil.checkPassword(vecchiaPassword, utente.getPassword())) {
                request.setAttribute("errore", "Password attuale errata");
                request.getRequestDispatcher("/modifica-password.jsp").forward(request, response);
                return;
            }
        } else {
            if (!PasswordUtil.checkPassword(vecchiaPassword, utente.getPassword())) {
                request.setAttribute("errore", "Password temporanea errata");
                request.getRequestDispatcher("/modifica-password.jsp?recupero=true").forward(request, response);
                return;
            }
        }

        boolean esito = UserDAO.aggiornaUtenteConPassword(utente, nuovaPassword);

        if (esito) {
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=Password modificata correttamente");
        } else {
            request.setAttribute("errore", "Errore durante la modifica");
            request.getRequestDispatcher("/modifica-password.jsp").forward(request, response);
        }
    }
}
