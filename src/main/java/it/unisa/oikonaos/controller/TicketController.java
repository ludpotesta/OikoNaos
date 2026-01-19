package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.dao.AllegatoDAO;
import it.unisa.oikonaos.model.Ticket;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

@WebServlet(name = "TicketController", value = "/TicketController")
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,        // 5MB per file
        maxRequestSize = 25 * 1024 * 1024     // max 5 file
)
public class TicketController extends HttpServlet {
    // CARTELLA FISICA DEGLI UPLOAD
    private static final String UPLOAD_DIR = "C:/OikoNaos/uploads";

    //VISUALIZZAZIONE TICKET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (!"COINQUILINO".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/home.jsp?error=permessi");
            return;
        }

        String action = request.getParameter("action");

        try {
            TicketDAO dao = new TicketDAO();

            // Apri pagina nuovo ticket
            if ("new".equals(action)) {
                request.getRequestDispatcher("nuovoTicket.jsp")
                        .forward(request, response);
                return;
            }

            // Lista ticket (action=list o default)
            if (action == null || "list".equals(action)) {
                request.setAttribute(
                        "listaTicket",
                        dao.doRetrieveByAutore(utente.getIdUtente())
                );

                request.getRequestDispatcher("ticket.jsp")
                        .forward(request, response);
                return;
            } else if ("details".equals(action)) {

            long idTicket;

            try {
                idTicket = Long.parseLong(request.getParameter("idTicket"));
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/TicketController");
                return;
            }

            Ticket ticket = dao.doRetrieveByIdAndUtente(
                    idTicket,
                    utente.getIdUtente()
            );

            if (ticket == null) {
                response.sendRedirect(request.getContextPath() + "/TicketController");
                return;
            }

            AllegatoDAO allegatoDAO = new AllegatoDAO();
            request.setAttribute(
                    "allegati",
                    allegatoDAO.doRetrieveByTicket(idTicket)
            );

            request.setAttribute("ticket", ticket);
            request.getRequestDispatcher("/dettagliTicket.jsp")
                    .forward(request, response);
            return;
        }

        // action non riconosciuta
            response.sendRedirect("home.jsp");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }


    //CREAZIONE / CANCELLAZIONE
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null)
                ? (Utente) session.getAttribute("utente")
                : null;

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");

        try {
            TicketDAO ticketDAO = new TicketDAO();

            // =====================
            // CANCELLA TICKET
            // =====================
            if ("delete".equals(action)) {
                long idTicket = Long.parseLong(request.getParameter("idTicket"));

                boolean deleted = ticketDAO.deleteTicketIfAperto(
                        idTicket,
                        utente.getIdUtente()
                );

                response.sendRedirect(
                        deleted
                                ? "TicketController?msg=deleted"
                                : "TicketController?error=not_deletable"
                );
                return;
            }

            // =====================
            // CREA TICKET
            // =====================
            String titolo = request.getParameter("titolo");
            String descrizione = request.getParameter("descrizione");
            String categoria = request.getParameter("categoria");
            String priorita = request.getParameter("priorita");

            if (titolo == null || titolo.isBlank()) {
                response.sendRedirect("nuovoTicket.jsp?error=campi");
                return;
            }

            long idTicket = ticketDAO.creaTicket(
                    titolo,
                    descrizione,
                    categoria,
                    priorita,
                    utente.getIdUtente()
            );

            // =====================
            // GESTIONE ALLEGATI
            // =====================
            AllegatoDAO allegatoDAO = new AllegatoDAO();

            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            int count = 0;

            for (Part part : request.getParts()) {
                if (!"allegati".equals(part.getName()) || part.getSize() == 0) {
                    continue;
                }

                if (count >= 5) break;

                String fileName = Paths.get(part.getSubmittedFileName())
                        .getFileName().toString();

                File file = new File(uploadDir, fileName);
                part.write(file.getAbsolutePath());

                // SALVIAMO IL PERCORSO LOGICO
                allegatoDAO.salva(
                        fileName,
                        file.getAbsolutePath(), // ✅ PERCORSO REALE
                        part.getContentType(),
                        idTicket
                );

                count++;
            }

            response.sendRedirect("TicketController");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("nuovoTicket.jsp?error=generico");
        }
    }
}
