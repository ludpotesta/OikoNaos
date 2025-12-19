package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "PrenotazioneControl", value = "/PrenotazioneControl")
public class PrenotazioneControl extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Recupero l'utente dalla sessione per associarlo alla prenotazione [cite: 293]
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // 2. Recupero i parametri dal form della pagina JSP [cite: 275]
            Date dataScelta = Date.valueOf(request.getParameter("data"));
            long idPostazione = Long.parseLong(request.getParameter("idPostazione"));
            long idFascia = Long.parseLong(request.getParameter("idFascia"));

            PrenotazioneDAO dao = new PrenotazioneDAO();

            // 3. LOGICA ODD: Verifica se la postazione è già occupata in quella fascia [cite: 223, 340]
            if (dao.verificaConflitto(dataScelta, idPostazione, idFascia)) {
                // Se c'è un conflitto, rimando alla pagina con un messaggio di errore
                response.sendRedirect("prenotazione.jsp?error=conflitto");
                return;
            }

            // 4. Se è libera, creo l'oggetto Prenotazione [cite: 152, 314]
            Prenotazione nuovaPrenotazione = new Prenotazione();
            nuovaPrenotazione.setData(dataScelta);
            nuovaPrenotazione.setIdUtente(utenteLoggato.getIdUtente());
            nuovaPrenotazione.setIdPostazione(idPostazione);
            nuovaPrenotazione.setIdFasciaOraria(idFascia);

            // 5. Salvo nel Database tramite il DAO [cite: 340]
            dao.creaPrenotazione(nuovaPrenotazione);

            // Ritorno alla home con successo
            response.sendRedirect("home.jsp?success=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("prenotazione.jsp?error=generico");
        }
    }
}
