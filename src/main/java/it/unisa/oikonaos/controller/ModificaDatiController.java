package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "ModificaDatiController", value = "/ModificaDatiController")
public class ModificaDatiController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente =
                (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/modificaDati.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utenteSessione =
                (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utenteSessione == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            String nome = request.getParameter("nome");
            String cognome = request.getParameter("cognome");
            String email = request.getParameter("email");
            String telefono = request.getParameter("telefono");

            if (nome == null || cognome == null || email == null ||
                    nome.isBlank() || cognome.isBlank() || email.isBlank()) {

                response.sendRedirect("home.jsp?error=campi");
                return;
            }

            // Creo oggetto aggiornato
            Utente u = new Utente();
            u.setIdUtente(utenteSessione.getIdUtente());
            u.setNome(nome);
            u.setCognome(cognome);
            u.setEmail(email);
            u.setTelefono(telefono);

            // Update DB
            UserDAO dao = new UserDAO();
            dao.updateProfilo(u);

            // Aggiorno sessione
            utenteSessione.setNome(nome);
            utenteSessione.setCognome(cognome);
            utenteSessione.setEmail(email);
            utenteSessione.setTelefono(telefono);

            session.setAttribute("utente", utenteSessione);

            // Redirect
            response.sendRedirect("home.jsp?success=profilo");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home.jsp?error=generico");
        }
    }
}
