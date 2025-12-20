package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.PrenotazioneDAO;
import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.sql.Date;

@WebServlet(name = "PrenotazioneController", value = "/PrenotazioneController")
public class PrenotazioneController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Gestisce la visualizzazione delle prenotazioni (Persona 1)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente != null) {
            try {
                PrenotazioneDAO dao = new PrenotazioneDAO();
                List<Prenotazione> miePrenotazioni = dao.doRetrieveByUtente(utente.getIdUtente());
                request.setAttribute("listaPrenotazioni", miePrenotazioni);
                request.getRequestDispatcher("prenotazioni.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("profilo.jsp?error=db");
            }
        } else {
            response.sendRedirect("login.jsp");
        }
    }

    /**
     * Gestisce la creazione e l'annullamento delle prenotazioni (Persona 1)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utente utenteLoggato = (Utente) session.getAttribute("utente");

        if (utenteLoggato == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        PrenotazioneDAO dao = new PrenotazioneDAO();

        try {
            if ("delete".equals(action)) {
                // LOGICA ANNULLA
                long idPrenotazione = Long.parseLong(request.getParameter("idPrenotazione"));
                dao.doDelete(idPrenotazione);
                response.sendRedirect("PrenotazioneController");
            } else {
                // LOGICA CREA
                String dataStr = request.getParameter("data");
                String postazioneStr = request.getParameter("idPostazione");
                String fasciaStr = request.getParameter("idFascia");

                if (dataStr != null && postazioneStr != null && fasciaStr != null) {
                    Date dataScelta = Date.valueOf(dataStr);
                    long idPostazione = Long.parseLong(postazioneStr);
                    long idFascia = Long.parseLong(fasciaStr);

                    // Controllo conflitti richiesto dall'ODD
                    if (dao.verificaConflitto(dataScelta, idPostazione, idFascia)) {
                        response.sendRedirect("nuovaPrenotazione.jsp?error=conflitto");
                        return;
                    }

                    Prenotazione p = new Prenotazione();
                    p.setData(dataScelta);
                    p.setIdUtente(utenteLoggato.getIdUtente());
                    p.setIdPostazione(idPostazione);
                    p.setIdFasciaOraria(idFascia);

                    dao.creaPrenotazione(p);
                    response.sendRedirect("PrenotazioneController");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("profilo.jsp?error=generico");
        }
    }
}
