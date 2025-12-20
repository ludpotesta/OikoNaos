package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.model.Ticket;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TicketController", value = "/TicketController")
public class TicketController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            TicketDAO dao = new TicketDAO();
            List<Ticket> lista = dao.doRetrieveByAutore(utente.getIdUtente());
            request.setAttribute("listaTicket", lista);
            request.getRequestDispatcher("ticket.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("home.jsp?error=db");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String titolo = request.getParameter("titolo");
        String descrizione = request.getParameter("descrizione");
        String categoria = request.getParameter("categoria");
        String priorita = request.getParameter("priorita");
        String action = request.getParameter("action");

        try {
            TicketDAO dao = new TicketDAO();

            if ("delete".equals(action)) {
                long idTicket = Long.parseLong(request.getParameter("idTicket"));
                boolean ok = dao.deleteTicketIfAperto(idTicket, utente.getIdUtente());

                if (!ok) {
                    response.sendRedirect("TicketController?error=not_deletable");
                } else {
                    response.sendRedirect("TicketController?msg=deleted");
                }
                return;
            }

            dao.creaTicket(titolo, descrizione, categoria, priorita, utente.getIdUtente());
            response.sendRedirect("TicketController"); // Dopo aver creato, torna alla lista
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("nuovoTicket.jsp?error=true");
        }
    }
}