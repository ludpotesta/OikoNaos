package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminTicketController", value = "/AdminTicketController")
public class AdminTicketController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utente utente = (Utente) request.getSession().getAttribute("utente");

        // Protezione: Solo ADMIN
        if (utente == null || !utente.getRuolo().equalsIgnoreCase("ADMIN")) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            TicketDAO dao = new TicketDAO();
            List<Ticket> lista = dao.doRetrieveAll();
            request.setAttribute("listaGlobaleTicket", lista);
            request.getRequestDispatcher("admin/ticket.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("updateStato".equals(action)) {
            long id = Long.parseLong(request.getParameter("idTicket"));
            String nuovoStato = request.getParameter("nuovoStato");
            try {
                new TicketDAO().updateStato(id, nuovoStato);
                response.sendRedirect("AdminTicketController");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
