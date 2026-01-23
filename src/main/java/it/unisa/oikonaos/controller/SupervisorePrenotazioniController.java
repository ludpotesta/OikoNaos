package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.PrenotazioneDAO;
import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SupervisorePrenotazioniController", value = "/SupervisorePrenotazioniController")
public class SupervisorePrenotazioniController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        // PROTEZIONE: Se non è supervisore, torna alla home o al login
        if (utente == null || !utente.getRuolo().equalsIgnoreCase("SUPERVISORE")) {
            response.sendRedirect(request.getContextPath() + "/homeSupervisore.jsp?error=ruolo");
            return;
        }

        try {
            PrenotazioneDAO dao = new PrenotazioneDAO();
            // Usiamo il metodo doRetrieveAll() che abbiamo aggiunto al DAO prima
            List<Prenotazione> tutteLePrenotazioni = dao.doRetrieveAll();

            request.setAttribute("listaGlobalePrenotazioni", tutteLePrenotazioni);
            request.getRequestDispatcher("supervisore/prenotazioniSupervisore.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("homeSupervisore.jsp?error=db");
        }
    }
}
