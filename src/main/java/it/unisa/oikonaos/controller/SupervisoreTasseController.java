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
            if (trimestre == null || trimestre.isBlank()) {
                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTasseController?error=trimestre"
                );
                return;
            }

            trimestre = trimestre.trim();
            if (trimestre.length() > 20) {
                throw new IllegalArgumentException("Trimestre troppo lungo");
            }

            String tipo = request.getParameter("tipo"); // ORDINARIA / STRAORDINARIA
            String destinatario = request.getParameter("destinatario");

            if (destinatario == null || destinatario.isBlank()) {
                destinatario = "TUTTI"; // default logico
            }

            double importo = Double.parseDouble(request.getParameter("importo"));
            Date scadenza = Date.valueOf(request.getParameter("scadenza"));

            /* TASSA SINGOLA */
            if ("SINGOLO".equals(destinatario)) {

                String idParam = request.getParameter("idUtente");
                if (idParam == null || idParam.isBlank()) {
                    throw new IllegalArgumentException("Coinquilino non selezionato");
                }

                Long idUtente = Long.parseLong(idParam);

                if (idUtente.equals(utente.getIdUtente())) {
                    throw new IllegalArgumentException(
                            "Non puoi assegnare una tassa a te stesso"
                    );
                }

                tassaDAO.creaTassa(
                        trimestre,
                        importo,
                        scadenza,
                        tipo,
                        idUtente
                );

            /* TASSA GLOBALE */
            } else if ("TUTTI".equals(destinatario)) {

                tassaDAO.creaTassa(
                        trimestre,
                        importo,
                        scadenza,
                        tipo,
                        null
                );
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
