package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.EventoDAO;
import it.unisa.oikonaos.dto.EventoBachecaDTO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "BachecaEventiController", value = "/BachecaEventiController")
public class BachecaEventiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null)
                ? (Utente) session.getAttribute("utente")
                : null;

        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            EventoDAO dao = new EventoDAO();

            List<EventoBachecaDTO> eventi =
                    dao.getEventiBacheca(u.getIdUtente());

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
