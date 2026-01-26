package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.AllegatoDAO;
import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.Ticket;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
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
                request.getRequestDispatcher("nuovoTicket.jsp")
                        .forward(request, response);

            } else if ("details".equals(action)) {

                long idTicket = Long.parseLong(request.getParameter("idTicket"));

                // recupero ticket
                Ticket ticket = dao.doRetrieveById(idTicket);
                if (ticket == null || ticket.getIdAutore() != utente.getIdUtente()) {
                    response.sendRedirect("TicketController");
                    return;
                }

                request.setAttribute("ticket", ticket);
                AllegatoDAO allegatoDAO = new AllegatoDAO();
                request.setAttribute(
                        "allegati",
                        allegatoDAO.doRetrieveByTicket(idTicket)
                );
                request.getRequestDispatcher("dettagliTicket.jsp")
                        .forward(request, response);
            } else {
                // Recupera solo i ticket dell'utente
                request.setAttribute("listaTicket",
                        dao.doRetrieveByAutore(utente.getIdUtente()));

                request.getRequestDispatcher("ticket.jsp")
                        .forward(request, response);
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

                long idTicket = dao.creaTicket(
                        titolo, descrizione, categoria, priorita,
                        utente.getIdUtente()
                );

                AllegatoDAO allegatoDAO = new AllegatoDAO();
                for (Part part : request.getParts()) {
                    if ("allegati".equals(part.getName()) && part.getSize() > 0) {

                        String nomeFile = part.getSubmittedFileName();
                        String tipoFile = part.getContentType();

                        String uploadDir = "C:/OikoNaos/uploads";
                        File dir = new File(uploadDir);
                        if (!dir.exists()) dir.mkdirs();

                        File file = new File(uploadDir, nomeFile);
                        part.write(file.getAbsolutePath());

                        allegatoDAO.salva(
                                nomeFile,
                                file.getAbsolutePath(),
                                tipoFile,
                                idTicket
                        );
                    }
                }

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
