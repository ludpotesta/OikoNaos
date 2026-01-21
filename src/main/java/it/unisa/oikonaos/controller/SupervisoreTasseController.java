package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TassaDAO;
import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.TassaTrimestrale;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(name = "SupervisoreTasseController", value = "/SupervisoreTasseController")
public class SupervisoreTasseController extends HttpServlet {

    private TassaDAO tassaDAO;
    private UserDAO utenteDAO;

    @Override
    public void init() {
        tassaDAO = new TassaDAO();
        utenteDAO = new UserDAO();
    }

    /* ===========================
       GET – VISUALIZZAZIONE PAGINA
       =========================== */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null)
                ? (Utente) session.getAttribute("utente")
                : null;

        if (utente == null || !"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            List<TassaTrimestrale> tasse = tassaDAO.doRetrieveAll();
            request.setAttribute("tasse", tasse);

            // Coinquilini per eventuali penali singole
            List<Utente> coinquilini =
                    utenteDAO.doRetrieveCoinquiliniEscluso(utente.getIdUtente());
            request.setAttribute("coinquilini", coinquilini);

            request.getRequestDispatcher("/supervisore/tasseSupervisore.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/supervisore/home.jsp?error=tasse"
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

        if (utente == null || !"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            String trimestre = request.getParameter("trimestre");
            trimestre = trimestre.trim();

            if (trimestre.length() > 20) {
                throw new IllegalArgumentException(
                        "Trimestre troppo lungo: " + trimestre
                );
            }

            System.out.println("DEBUG TRIMESTRE = [" + trimestre + "]");
            System.out.println("LUNGHEZZA = " + trimestre.length());

            String tipo = request.getParameter("tipo"); // ORDINARIA / STRAORDINARIA
            String destinatario = request.getParameter("destinatario");

            if (trimestre == null || trimestre.isBlank()) {
                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTasseController?error=trimestre"
                );
                return;
            }

            double importo = Double.parseDouble(request.getParameter("importo"));
            Date scadenza = Date.valueOf(request.getParameter("scadenza"));

            Long idUtente = null;
            if ("SINGOLO".equals(destinatario)) {
                String id = request.getParameter("idUtente");
                if (id != null && !id.isBlank()) {
                    idUtente = Long.parseLong(id);
                }
            }

            tassaDAO.creaTassa(trimestre, importo, scadenza, tipo, idUtente);
            if (idUtente != null && idUtente.equals(utente.getIdUtente())) {
                throw new IllegalArgumentException("Non puoi assegnare una tassa a te stesso");
            }

            response.sendRedirect(
                    request.getContextPath() + "/SupervisoreTasseController"
            );

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/SupervisoreTasseController?error=create"
            );
        }
    }
}
