package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "TicketController", value = "/TicketController")
@MultipartConfig
public class TicketController extends HttpServlet {

    // LOGICA COINQUILINO (Solo i propri ticket)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
            if ("new".equals(action)) {
                request.getRequestDispatcher("nuovoTicket.jsp").forward(request, response);
            } else {
                // Recupera solo i ticket dell'utente
                request.setAttribute("listaTicket", dao.doRetrieveByAutore(utente.getIdUtente()));
                request.getRequestDispatcher("ticket.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

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
            if ("create".equals(action)) {
                String titolo = request.getParameter("titolo");
                String descrizione = request.getParameter("descrizione");
                String categoria = request.getParameter("categoria");
                String priorita = request.getParameter("priorita");

                dao.creaTicket(titolo, descrizione, categoria, priorita, utente.getIdUtente());
                response.sendRedirect("TicketController");

            } else if ("delete".equals(action)) {
                long idTicket = Long.parseLong(request.getParameter("idTicket"));
                dao.deleteTicketIfAperto(idTicket, utente.getIdUtente());
                response.sendRedirect("TicketController");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("nuovoTicket.jsp?error=generic");
        }
    }
}
