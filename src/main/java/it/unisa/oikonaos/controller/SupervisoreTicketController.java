package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SupervisoreTicketController", value = "/SupervisoreTicketController")
public class SupervisoreTicketController extends HttpServlet {

    // --- LOGICA SUPERVISORE: VEDO TUTTI I TICKET DI TUTTI ---
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        // Controllo Ruolo (Adatta "SUPERVISORE" se nel DB lo chiami diversamente, es. "Azienda")
        if (utente == null /* || !utente.getRuolo().equalsIgnoreCase("SUPERVISORE") */) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        TicketDAO dao = new TicketDAO();

        try {
            if ("dettagli".equals(action)) {
                // Dettaglio singolo ticket
                long id = Long.parseLong(request.getParameter("id"));
                request.setAttribute("ticket", dao.doRetrieveById(id));
                request.getRequestDispatcher("/WEB-INF/supervisore/dettagli-ticket.jsp").forward(request, response);
            } else {
                // LISTA GLOBALE
                // Nota: Usa doRetrieveAll
                request.setAttribute("listaTicket", dao.doRetrieveAll());
                // Manda alla cartella /WEB-INF/supervisore/
                request.getRequestDispatcher("/WEB-INF/supervisore/gestione-ticket.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Errore supervisione", e);
        }
    }

    // --- LOGICA SUPERVISORE: CAMBIO LO STATO ---
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        TicketDAO dao = new TicketDAO();

        try {
            if ("updateStato".equals(action)) {
                long idTicket = Long.parseLong(request.getParameter("idTicket"));

                // Nota: Assicurati che nella JSP la select si chiami "stato"
                String nuovoStato = request.getParameter("stato");

                dao.updateStato(idTicket, nuovoStato);

                // Ricarica la pagina del Supervisore
                response.sendRedirect("SupervisoreTicketController?msg=updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("SupervisoreTicketController?error=generic");
        }
    }
}