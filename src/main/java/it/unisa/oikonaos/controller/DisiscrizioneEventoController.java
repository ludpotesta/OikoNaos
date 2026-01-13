package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.EventoDAO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "DisiscrizioneEventoController", value = "/DisiscrizioneEventoController")
public class DisiscrizioneEventoController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String idEventoParam = request.getParameter("idEvento");

        if (idEventoParam == null) {
            response.sendRedirect(request.getContextPath() + "/BachecaEventiController");
            return;
        }

        try {
            long idEvento = Long.parseLong(idEventoParam);
            long idUtente = u.getIdUtente();

            EventoDAO dao = new EventoDAO();
            dao.disiscriviUtenteDaEvento(idEvento, idUtente);

            // ritorno alla bacheca eventi
            response.sendRedirect(request.getContextPath() + "/BachecaEventiController");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/BachecaEventiController?error=disiscrizione"
            );
        }
    }
}
