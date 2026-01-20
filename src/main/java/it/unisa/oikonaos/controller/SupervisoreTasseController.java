package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.TassaDAO;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null || !"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            TassaDAO tassaDAO = new TassaDAO();
            List<TassaTrimestrale> tasse = tassaDAO.doRetrieveAll();

            request.setAttribute("tasse", tasse);
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
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null || !"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            String trimestre = request.getParameter("trimestre");
            double importo = Double.parseDouble(request.getParameter("importo"));
            Date scadenza = Date.valueOf(request.getParameter("scadenza"));

            TassaDAO tassaDAO = new TassaDAO();
            tassaDAO.creaTassa(trimestre, importo, scadenza);

            if (trimestre == null || trimestre.isBlank()) {
                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTasseController?error=trimestre"
                );
                return;
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
