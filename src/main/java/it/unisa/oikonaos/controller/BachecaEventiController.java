package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.EventoDAO;
import it.unisa.oikonaos.dto.EventoBachecaDTO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "BachecaEventiController", value = "/BachecaEventiController")
public class BachecaEventiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            EventoDAO dao = new EventoDAO();
            List<EventoBachecaDTO> eventi =
                    dao.getEventiBacheca(u.getIdUtente());

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (EventoBachecaDTO e : eventi) {

                boolean eventoPassato =
                        e.getDataFine() != null && e.getDataFine().isBefore(now);

                boolean iscrivibile =
                        !eventoPassato
                                && !e.isIscritto()
                                && e.getPostiDisponibili() > 0;

                boolean disiscrivibile =
                        !eventoPassato
                                && e.isIscritto();

                if (e.getDataInizio() != null) {
                    e.setDataInizioFormatted(
                            e.getDataInizio().format(formatter)
                    );
                }

                if (e.getDataFine() != null) {
                    e.setDataFineFormatted(
                            e.getDataFine().format(formatter)
                    );
                }

                e.setIscrivibile(iscrivibile);
                e.setDisiscrivibile(disiscrivibile);
            }

            request.setAttribute("eventi", eventi);
            request.getRequestDispatcher("/bachecaEventi.jsp")
                    .forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/home.jsp?error=generico"
            );
        }
    }
}
