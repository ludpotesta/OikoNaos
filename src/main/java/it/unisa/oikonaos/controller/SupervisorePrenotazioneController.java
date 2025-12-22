package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.*;
import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminPrenotazioneController", value = "/AdminPrenotazioneController")
public class SupervisorePrenotazioneController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Utente u = (Utente) req.getSession().getAttribute("utente");

        if (u == null || !u.getRuolo().equals("SUPERVISORE")) {
            resp.sendError(403);
            return;
        }

        try {
            PrenotazioneDAO prenotazioneDAO = new PrenotazioneDAO();
            List<Prenotazione> prenotazioni = prenotazioneDAO.findAllByComunita(u.getIdComunita());

            req.setAttribute("prenotazioni", prenotazioni);
            req.getRequestDispatcher("/admin/gestionePrenotazioni.jsp")
                    .forward(req, resp);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}