package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.dao.AllegatoDAO;
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


    //VISUALIZZAZIONE TICKET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
            TicketDAO dao = new TicketDAO();

            // Apri pagina nuovo ticket
            if ("new".equals(action)) {
                request.getRequestDispatcher("nuovoTicket.jsp")
                        .forward(request, response);
                return;
            }

            // Lista ticket utente
            request.setAttribute(
                    "listaTicket",
                    dao.doRetrieveByAutore(utente.getIdUtente())
            );

            request.getRequestDispatcher("ticket.jsp")
                    .forward(request, response);

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

            //CANCELLA TICKET
            if ("delete".equals(action)) {
                long idTicket = Long.parseLong(request.getParameter("idTicket"));

                boolean deleted = ticketDAO.deleteTicketIfAperto(
                        idTicket,
                        utente.getIdUtente()
                );

                if (!deleted) {
                    response.sendRedirect("TicketController?error=not_deletable");
                } else {
                    response.sendRedirect("TicketController?msg=deleted");
                }
                return;
            }

            //CREA NUOVO TICKET
            String titolo = request.getParameter("titolo");
            String descrizione = request.getParameter("descrizione");
            String categoria = request.getParameter("categoria");
            String priorita = request.getParameter("priorita");

            if (titolo == null || titolo.isBlank()) {
                response.sendRedirect("nuovoTicket.jsp?error=campi");
                return;
            }

            // Crea ticket
            long idTicket = ticketDAO.creaTicket(
                    titolo,
                    descrizione,
                    categoria,
                    priorita,
                    utente.getIdUtente()
            );

            /* =========================
               GESTIONE ALLEGATI (MAX 5)
               ========================= */
            AllegatoDAO allegatoDAO = new AllegatoDAO();
            Collection<Part> parts = request.getParts();
            int count = 0;

            String uploadPath = getServletContext().getRealPath("/uploads");
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();

            for (Part part : parts) {
                if ("allegati".equals(part.getName()) && part.getSize() > 0) {

                    if (count >= 5) break;

                    String fileName = Paths.get(part.getSubmittedFileName())
                            .getFileName().toString();

                    File file = new File(dir, fileName);
                    part.write(file.getAbsolutePath());

                    allegatoDAO.salva(
                            fileName,
                            "uploads/" + fileName,
                            part.getContentType(),
                            idTicket
                    );

                    count++;
                }
            }

            // Torna alla lista ticket
            response.sendRedirect("TicketController");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("nuovoTicket.jsp?error=generico");
        }
    }
}
