package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.dao.AllegatoDAO;
import it.unisa.oikonaos.dao.AggiornamentoTicketDAO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "SupervisoreTicketController", value = "/SupervisoreTicketController")
public class SupervisoreTicketController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null || !utente.getRuolo().equalsIgnoreCase("SUPERVISORE")) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        TicketDAO dao = new TicketDAO();

        String stato = request.getParameter("stato");
        String priorita = request.getParameter("priorita");
        String dataCreazione = request.getParameter("dataCreazione");

        try {
            if ("details".equals(action)) {

                long id = Long.parseLong(request.getParameter("idTicket"));
                var ticket = dao.doRetrieveById(id);

                if (ticket == null) {
                    response.sendRedirect("SupervisoreTicketController");
                    return;
                }

                request.setAttribute("ticket", ticket);
                AllegatoDAO allegatoDAO = new AllegatoDAO();
                request.setAttribute(
                        "allegati",
                        allegatoDAO.doRetrieveByTicket(id)
                );

                AggiornamentoTicketDAO storicoDAO = new AggiornamentoTicketDAO();
                request.setAttribute(
                        "storico",
                        storicoDAO.doRetrieveByTicket(id)
                );
                request.getRequestDispatcher("/supervisore/dettagliTicketSupervisore.jsp")
                        .forward(request, response);

            } else {

                request.setAttribute(
                        "listaTicket",
                        dao.doRetrieveFiltered(stato, priorita, dataCreazione)
                );
                request.getRequestDispatcher("/supervisore/ticketSupervisore.jsp")
                        .forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Errore supervisione", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null || !utente.getRuolo().equalsIgnoreCase("SUPERVISORE")) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        TicketDAO dao = new TicketDAO();

        try {
            if ("updateStato".equals(action)) {

                long idTicket = Long.parseLong(request.getParameter("idTicket"));
                String nuovoStato = request.getParameter("nuovoStato");

                dao.updateStato(idTicket, nuovoStato);
                //SALVATAGGIO STORICO
                AggiornamentoTicketDAO storicoDAO = new AggiornamentoTicketDAO();
                storicoDAO.creaAggiornamento(
                        idTicket,
                        utente.getIdUtente(),
                        "Stato aggiornato a: " + nuovoStato.replace("_", " ")
                );
                response.sendRedirect("SupervisoreTicketController?msg=updated");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("SupervisoreTicketController?error=generic");
        }
    }
}