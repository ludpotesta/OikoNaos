package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SupervisoreTicketController", value = "/SupervisoreTicketController")
public class SupervisoreTicketController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utente utente = (Utente) request.getSession().getAttribute("utente");

        // Protezione: solo SUPERVISORE
        if (utente == null || !"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            TicketDAO dao = new TicketDAO();
            List<Ticket> lista = dao.doRetrieveAll();
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

        String action = request.getParameter("action");

        if ("updateStato".equals(action)) {
            try {
                long id = Long.parseLong(request.getParameter("idTicket"));
                String nuovoStato = request.getParameter("nuovoStato");

                new TicketDAO().updateStato(id, nuovoStato);

                //redirect corretto
                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTicketController"
                );

            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTicketController?error=update"
                );
            }
        }
    }
}

