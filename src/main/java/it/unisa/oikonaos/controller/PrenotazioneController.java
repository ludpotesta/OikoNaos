package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.*;
import it.unisa.oikonaos.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.sql.Date;

@WebServlet(name = "PrenotazioneController", value = "/PrenotazioneController")
public class PrenotazioneController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            PrenotazioneDAO dao = new PrenotazioneDAO();
            request.setAttribute(
                    "listaPrenotazioni",
                    dao.doRetrieveByUtente(u.getIdUtente())
            );
            request.getRequestDispatcher("prenotazioni.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

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

        try {
            PrenotazioneDAO dao = new PrenotazioneDAO();

            String action = request.getParameter("action");

            // ===== ANNULLA =====
            if ("delete".equals(action)) {
                long idPrenotazione =
                        Long.parseLong(request.getParameter("idPrenotazione"));

                dao.doDelete(idPrenotazione);
                response.sendRedirect("PrenotazioneController");
                return;
            }

            // ===== CREA PRENOTAZIONE =====
            String dataStr = request.getParameter("data");
            String postazioneStr = request.getParameter("idPostazione");
            String fasciaStr = request.getParameter("idFascia");

            if (dataStr == null || postazioneStr == null || fasciaStr == null) {
                response.sendRedirect("nuovaPrenotazione.jsp?error=campi");
                return;
            }

            Date data = Date.valueOf(dataStr);
            if (data.before(Date.valueOf(java.time.LocalDate.now()))) {
                response.sendRedirect("nuovaPrenotazione.jsp?error=data_passata");
                return;
            }

            long idPostazione = Long.parseLong(postazioneStr);
            long idFascia = Long.parseLong(fasciaStr);

            if (dao.verificaConflitto(data, idPostazione, idFascia)) {
                response.sendRedirect("nuovaPrenotazione.jsp?error=conflitto");
                return;
            }

            Prenotazione p = new Prenotazione();
            p.setData(data);
            p.setStato("ATTIVA");
            p.setIdUtente(utente.getIdUtente());
            p.setIdPostazione(idPostazione);
            p.setIdFasciaOraria(idFascia);

            dao.creaPrenotazione(p);

            response.sendRedirect("PrenotazioneController");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("profilo.jsp?error=generico");
        }
    }

}
