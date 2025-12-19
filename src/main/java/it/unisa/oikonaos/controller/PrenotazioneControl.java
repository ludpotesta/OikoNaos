package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "PrenotazioneControl", value = "/PrenotazioneControl")
public class PrenotazioneControl extends HttpServlet {

    /**
     * Funzionalità: Visualizzare le prenotazioni dell'utente loggato (Persona 1)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Criterio richiesto: recupero utente dalla sessione
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente != null) {
            // Reindirizza alla pagina della lista (prenotazioni.jsp)
            request.getRequestDispatcher("prenotazioni.jsp").forward(request, response);
        } else {
            // Se non è loggato, rimanda al login
            response.sendRedirect("login.jsp");
        }
    }

    /**
     * Funzionalità: Creare una nuova prenotazione (Persona 1)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Criterio richiesto: recupero utente dalla sessione
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Recupero parametri dal form (quelli delle JSP nuovaPrenotazione.jsp)
            Date dataScelta = Date.valueOf(request.getParameter("data"));
            long idPostazione = Long.parseLong(request.getParameter("idPostazione"));
            long idFascia = Long.parseLong(request.getParameter("idFascia"));

            PrenotazioneDAO dao = new PrenotazioneDAO();

            // Logica ODD: Verifica se la postazione è già occupata
            if (dao.verificaConflitto(dataScelta, idPostazione, idFascia)) {
                response.sendRedirect("nuovaPrenotazione.jsp?error=conflitto");
                return;
            }

            // Se libera, creo l'oggetto Prenotazione
            Prenotazione nuovaPrenotazione = new Prenotazione();
            nuovaPrenotazione.setData(dataScelta);
            nuovaPrenotazione.setIdUtente(utenteLoggato.getIdUtente());
            nuovaPrenotazione.setIdPostazione(idPostazione);
            nuovaPrenotazione.setIdFasciaOraria(idFascia);

            // Salvo nel Database tramite il DAO
            dao.creaPrenotazione(nuovaPrenotazione);

            // Ritorno alla home o alla lista con successo
            response.sendRedirect("home.jsp?success=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("nuovaPrenotazione.jsp?error=generico");
        }
    }
}
