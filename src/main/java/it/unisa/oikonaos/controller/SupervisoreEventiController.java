package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.EventoDAO;
import it.unisa.oikonaos.model.Evento;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SupervisoreEventiController", value = "/SupervisoreEventiController")
public class SupervisoreEventiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null || !"SUPERVISORE".equalsIgnoreCase(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        EventoDAO dao = new EventoDAO();
        String action = request.getParameter("action");

        try {

            if ("edit".equals(action)) {

                long idEvento = Long.parseLong(request.getParameter("id"));
                Evento e = dao.getEventoById(idEvento);

                if (e == null) {
                    response.sendRedirect(request.getContextPath() + "/SupervisoreEventiController");
                    return;
                }

                // evento passato → bloccato
                if (e.getDataFine() != null &&
                        e.getDataFine().isBefore(LocalDateTime.now())) {

                    response.sendRedirect(
                            request.getContextPath() + "/SupervisoreEventiController?error=locked"
                    );
                    return;
                }

                request.setAttribute("evento", e);
                request.getRequestDispatcher("/supervisore/modificaEvento.jsp")
                        .forward(request, response);
                return;
            }

            /* FORM NUOVO EVENTO */
            if ("new".equals(action)) {
                request.getRequestDispatcher("/supervisore/nuovoEvento.jsp")
                        .forward(request, response);
                return;
            }

            /* LISTA EVENTI */
            List<Evento> eventi = dao.getAllEventi();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            List<Object[]> eventiView = new ArrayList<>();

            for (Evento e : eventi) {

                String stato;
                if (e.getDataFine() != null && e.getDataFine().isBefore(now)) {
                    stato = "Passato";
                } else if (e.getDataInizio().isAfter(now)) {
                    stato = "Futuro";
                } else {
                    stato = "In corso";
                }

                String data = (e.getDataFine() != null)
                        ? e.getDataInizio().format(formatter)
                        + " – " + e.getDataFine().format(formatter)
                        : e.getDataInizio().format(formatter);

                String posti =
                        e.getPostiDisponibili() + " / " + e.getPostiTotali();

                eventiView.add(new Object[]{
                        e.getIdEvento(),
                        e.getTitolo(),
                        data,
                        e.getLuogo(),
                        posti,
                        stato
                });
            }

            request.setAttribute("eventi", eventiView);
            request.getRequestDispatcher("/supervisore/eventiSupervisore.jsp")
                    .forward(request, response);

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/SupervisoreEventiController?error=generic"
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null || !"SUPERVISORE".equalsIgnoreCase(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        EventoDAO dao = new EventoDAO();

        /* CANCELLA EVENTO */
        if ("delete".equals(action)) {
            try {
                String rawId = request.getParameter("idEvento");

                if (rawId == null || rawId.isBlank()) {
                    response.sendRedirect(
                            request.getContextPath() +
                                    "/SupervisoreEventiController?error=campi"
                    );
                    return;
                }

                long idEvento = Long.parseLong(rawId);
                dao.eliminaEvento(idEvento);   // HARD DELETE

                response.sendRedirect(
                        request.getContextPath() +
                                "/SupervisoreEventiController"
                );
                return;

            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(
                        request.getContextPath() +
                                "/SupervisoreEventiController?error=generic"
                );
                return;
            }
        }

    /* CREAZIONE NUOVO EVENTO */
        try {
            String titolo = request.getParameter("titolo");
            String descrizione = request.getParameter("descrizione");
            String luogo = request.getParameter("luogo");

            String postiParam = request.getParameter("posti");
            String dataInizioParam = request.getParameter("dataInizio");
            String dataFineParam = request.getParameter("dataFine");

            if (titolo == null || titolo.isBlank()
                    || postiParam == null || postiParam.isBlank()
                    || dataInizioParam == null || dataInizioParam.isBlank()) {

                response.sendRedirect(
                        request.getContextPath() +
                                "/SupervisoreEventiController?error=campi"
                );
                return;
            }

            int postiTotali = Integer.parseInt(postiParam);

            LocalDateTime dataInizio = LocalDateTime.parse(dataInizioParam);
            LocalDateTime dataFine = null;

            if (dataFineParam != null && !dataFineParam.isBlank()) {
                dataFine = LocalDateTime.parse(dataFineParam);
            }

            Evento evento = new Evento();
            evento.setTitolo(titolo);
            evento.setDescrizione(descrizione);
            evento.setLuogo(luogo);
            evento.setPostiTotali(postiTotali);
            evento.setPostiDisponibili(postiTotali);
            evento.setDataInizio(dataInizio);
            evento.setDataFine(dataFine);
            evento.setIdOrganizzatore(u.getIdUtente());

            dao.creaEvento(evento);

            response.sendRedirect(
                    request.getContextPath() +
                            "/SupervisoreEventiController"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() +
                            "/SupervisoreEventiController?error=generic"
            );
        }
    }

}
