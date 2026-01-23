package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.RichiestaRisorsaDAO;
import it.unisa.oikonaos.dao.RisorsaDAO;
import it.unisa.oikonaos.model.Risorsa;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "SupervisoreRisorseController", value = "/SupervisoreRisorseController")
public class SupervisoreRisorseController extends HttpServlet {

    private RisorsaDAO risorsaDAO;
    private RichiestaRisorsaDAO richiestaDAO;

    @Override
    public void init() {
        risorsaDAO = new RisorsaDAO();
        richiestaDAO = new RichiestaRisorsaDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            request.setAttribute("risorse", risorsaDAO.doRetrieveAll());
            request.setAttribute("richieste", richiestaDAO.doRetrieveAll());

            request.getRequestDispatcher(
                    "/supervisore/risorseSupervisore.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("inserisciRisorsa".equals(action)) {
                inserisciRisorsa(request);
            } else if ("accettaRichiesta".equals(action)) {
                richiestaDAO.aggiornaStato(
                        Long.parseLong(request.getParameter("idRichiesta")),
                        "APPROVATA"
                );
            } else if ("rifiutaRichiesta".equals(action)) {
                richiestaDAO.aggiornaStato(
                        Long.parseLong(request.getParameter("idRichiesta")),
                        "RIFIUTATA"
                );
            }

            response.sendRedirect("SupervisoreRisorseController");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void inserisciRisorsa(HttpServletRequest request) throws Exception {

        Risorsa r = new Risorsa();
        r.setNome(request.getParameter("nome"));
        r.setDescrizione(request.getParameter("descrizione"));
        r.setRegoleUso(request.getParameter("regoleUso"));

        String penale = request.getParameter("penale");
        if (penale != null && !penale.isBlank()) {
            r.setPenale(new BigDecimal(penale));
        }

        risorsaDAO.doSave(r);
    }
}