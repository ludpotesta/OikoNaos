package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.AllegatoDAO;
import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.Ticket;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.Date;


import java.io.IOException;
import java.util.List;

@WebServlet(name = "SupervisoreTicketController", value = "/SupervisoreTicketController")
public class SupervisoreTicketController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        // Solo SUPERVISORE
        if (utente == null || !"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        TicketDAO ticketDAO = new TicketDAO();

        try {

            /* DETTAGLI TICKET */
            if ("details".equals(action)) {

                long idTicket;
                try {
                    idTicket = Long.parseLong(request.getParameter("idTicket"));
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/SupervisoreTicketController");
                    return;
                }

                Ticket ticket = ticketDAO.doRetrieveByIdWithAutore(idTicket);
                if (ticket == null) {
                    response.sendRedirect(request.getContextPath() + "/SupervisoreTicketController");
                    return;
                }

                AllegatoDAO allegatoDAO = new AllegatoDAO();

                request.setAttribute("ticket", ticket);
                request.setAttribute(
                        "allegati",
                        allegatoDAO.doRetrieveByTicket(idTicket)
                );

                request.getRequestDispatcher("/supervisore/dettagliTicketSupervisore.jsp")
                        .forward(request, response);
                return;
            }

            /* LISTA TICKET (con filtri) */

            String stato = request.getParameter("stato");
            String priorita = request.getParameter("priorita");

            Date dataCreazione = null;

            try {
                if (request.getParameter("dataCreazione") != null &&
                        !request.getParameter("dataCreazione").isBlank()) {

                    dataCreazione = Date.valueOf(request.getParameter("dataCreazione"));
                }
            } catch (IllegalArgumentException ignored) {}


            List<Ticket> lista = ticketDAO.findByFiltri(
                    stato,
                    priorita,
                    dataCreazione
            );

            request.setAttribute("listaGlobaleTicket", lista);

            request.getRequestDispatcher("/supervisore/ticketSupervisore.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/supervisore/home.jsp?error=ticket"
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        // Solo SUPERVISORE
        if (utente == null || !"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        /* AGGIORNA STATO TICKET */
        if ("updateStato".equals(action)) {
            try {
                long idTicket = Long.parseLong(request.getParameter("idTicket"));
                String nuovoStato = request.getParameter("nuovoStato");

                new TicketDAO().updateStato(idTicket, nuovoStato);

                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTicketController"
                );
                return;

            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTicketController?error=update"
                );
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/SupervisoreTicketController");
    }
}
