package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.PrenotazioneDAO;
import it.unisa.oikonaos.model.Prenotazione;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "PrenotazioneController", value = "/PrenotazioneController")
public class PrenotazioneController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (!"COINQUILINO".equalsIgnoreCase(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/home.jsp?error=permessi");
            return;
        }

        String action = request.getParameter("action");
        PrenotazioneDAO dao = new PrenotazioneDAO();

        try {

            if (action == null || "list".equals(action)) {
                request.setAttribute(
                        "listaPrenotazioni",
                        dao.doRetrieveByUtente(u.getIdUtente())
                );
                request.getRequestDispatcher("/prenotazioni.jsp").forward(request, response);
                return;
            }

            if ("new".equals(action)) {
                request.getRequestDispatcher("/nuovaPrenotazione.jsp")
                        .forward(request, response);
                return;
            }

            if ("postazioni".equals(action)) {

                long idAmbiente = Long.parseLong(request.getParameter("idAmbiente"));

                response.setContentType("text/plain");
                PrintWriter out = response.getWriter();

                dao.doRetrievePostazioniByAmbiente(idAmbiente)
                        .forEach(p ->
                                out.println(p[0] + ";" + p[1])
                        );

                out.flush();
                return;
            }
            response.sendRedirect(request.getContextPath() + "/home.jsp");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null || !"COINQUILINO".equalsIgnoreCase(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/home.jsp?error=permessi");
            return;
        }

        String action = request.getParameter("action");
        PrenotazioneDAO dao = new PrenotazioneDAO();

        try {

            // CREAZIONE PRENOTAZIONE
            if ("create".equals(action)) {

                Date data = Date.valueOf(request.getParameter("data"));
                long idPostazione = Long.parseLong(request.getParameter("idPostazione"));
                long idFascia = Long.parseLong(request.getParameter("idFascia"));

                if (dao.verificaConflitto(data, idPostazione, idFascia)) {
                    response.sendRedirect(
                            request.getContextPath() +
                                    "/PrenotazioneController?action=new&error=conflitto"
                    );
                    return;
                }

                Prenotazione p = new Prenotazione();
                p.setData(data);
                p.setStato("ATTIVA");
                p.setIdUtente(u.getIdUtente());
                p.setIdPostazione(idPostazione);
                p.setIdFasciaOraria(idFascia);

                dao.creaPrenotazione(p);

                response.sendRedirect(
                        request.getContextPath() +
                                "/PrenotazioneController?action=list"
                );
                return;
            }

            //CANCELLAZIONE PRENOTAZIONE
            if ("delete".equals(action)) {

                long idPrenotazione =
                        Long.parseLong(request.getParameter("idPrenotazione"));

                dao.doDelete(idPrenotazione, u.getIdUtente());

                response.sendRedirect(
                        request.getContextPath() +
                                "/PrenotazioneController?action=list"
                );
                return;
            }
            response.sendRedirect(
                    request.getContextPath() +
                            "/PrenotazioneController?action=list"
            );

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
