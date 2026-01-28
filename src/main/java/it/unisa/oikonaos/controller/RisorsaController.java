package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.dao.RisorsaDAO;
import it.unisa.oikonaos.dao.RichiestaRisorsaDAO;
import it.unisa.oikonaos.model.Risorsa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "RisorsaController", value = "/RisorsaController")
public class RisorsaController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null)
                ? (Utente) session.getAttribute("utente")
                : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            RisorsaDAO risorsaDAO = new RisorsaDAO();
            RichiestaRisorsaDAO richiestaDAO = new RichiestaRisorsaDAO();

            // lista di tutte le risorse (NON filtrate)
            List<Risorsa> risorse = risorsaDAO.doRetrieveAll();
            request.setAttribute("risorseDisponibili", risorse);

            // richieste dell’utente
            request.setAttribute(
                    "richiesteAttive",
                    richiestaDAO.doRetrieveByUtente(utente.getIdUtente())
            );

            // data odierna
            request.setAttribute("oggi", LocalDate.now().toString());

            // mappa: idRisorsa -> lista date occupate
            Map<Long, List<String>> dateOccupate = new HashMap<>();

            for (Risorsa r : risorse) {
                List<String> date = richiestaDAO.getDateOccupate(r.getIdRisorsa())
                        .stream()
                        .map(d -> d.toString())
                        .toList();

                dateOccupate.put(r.getIdRisorsa(), date);
            }

            request.setAttribute("dateOccupate", dateOccupate);

            request.getRequestDispatcher("/risorse.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/home.jsp?error=risorse"
            );
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
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            long idRisorsa = Long.parseLong(request.getParameter("idRisorsa"));

            String dataParam = request.getParameter("data");
            if (dataParam == null || dataParam.isBlank()) {
                response.sendRedirect(
                        request.getContextPath() + "/RisorsaController?error=data"
                );
                return;
            }

            LocalDate giorno = Date.valueOf(dataParam).toLocalDate();

            if (giorno.isBefore(LocalDate.now())) {
                response.sendRedirect(
                        request.getContextPath() + "/RisorsaController?error=data_passata"
                );
                return;
            }

            if (request.getParameter("accettaRegole") == null) {
                response.sendRedirect(
                        request.getContextPath() + "/RisorsaController?error=regole"
                );
                return;
            }

            RichiestaRisorsaDAO richiestaDAO = new RichiestaRisorsaDAO();
            if (richiestaDAO.esisteConflitto(idRisorsa, giorno)) {
                response.sendRedirect(
                        request.getContextPath() + "/RisorsaController?error=disponibile"
                );
                return;
            }

            Date data = Date.valueOf(dataParam);

            richiestaDAO.creaRichiesta(
                    idRisorsa,
                    utente.getIdUtente(),
                    data,
                    data
            );

            response.sendRedirect(
                    request.getContextPath() + "/RisorsaController?success=true"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/RisorsaController?error=request"
            );
        }
    }
}
