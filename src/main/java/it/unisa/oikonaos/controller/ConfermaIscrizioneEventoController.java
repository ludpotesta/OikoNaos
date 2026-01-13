package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.EventoDAO;
import it.unisa.oikonaos.dto.EventoBachecaDTO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ConfermaIscrizioneEventoController", value = "/ConfermaIscrizioneEventoController")
public class ConfermaIscrizioneEventoController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (Utente) session.getAttribute("utente");

        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        long idEvento = Long.parseLong(request.getParameter("idEvento"));

        try {
            EventoDAO dao = new EventoDAO();
            EventoBachecaDTO evento = dao.getEventoById(idEvento, u.getIdUtente());

            request.setAttribute("evento", evento);
            request.getRequestDispatcher("/confermaIscrizioneEvento.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("bachecaEventi.jsp?error=generico");
        }
    }
}

