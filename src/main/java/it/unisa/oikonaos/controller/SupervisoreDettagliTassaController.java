package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.PagamentoDAO;
import it.unisa.oikonaos.dao.TassaDAO;
import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.TassaTrimestrale;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(
        name = "SupervisoreDettagliTassaController",
        value = "/SupervisoreDettagliTassaController"
)
public class SupervisoreDettagliTassaController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente supervisore = (Utente) session.getAttribute("utente");

        if (supervisore == null
                || !"SUPERVISORE".equalsIgnoreCase(supervisore.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            long idTassa = Long.parseLong(request.getParameter("idTassa"));

            TassaDAO tassaDAO = new TassaDAO();
            UserDAO userDAO = new UserDAO();
            PagamentoDAO pagamentoDAO = new PagamentoDAO();

            TassaTrimestrale tassa = tassaDAO.getTassaById(idTassa);

            if (tassa == null) {
                response.sendRedirect(
                        request.getContextPath() + "/SupervisoreTasseController?error=notfound"
                );
                return;
            }

            //COINQUILINI
            List<Utente> coinquilini =
                    userDAO.doRetrieveCoinquiliniEscluso(supervisore.getIdUtente());

            //UTENTI CHE HANNO PAGATO
            List<Long> utentiPaganti =
                    pagamentoDAO.getUtentiCheHannoPagato(idTassa);
            request.setAttribute("tassa", tassa);
            request.setAttribute("coinquilini", coinquilini);
            request.setAttribute("utentiPaganti", utentiPaganti);

            request.getRequestDispatcher("/supervisore/dettagliTasseSupervisore.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/SupervisoreTasseController?error=detail"
            );
        }
    }
}
