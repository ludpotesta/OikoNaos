package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.PagamentoDAO;
import it.unisa.oikonaos.dao.TassaDAO;
import it.unisa.oikonaos.model.Pagamento;
import it.unisa.oikonaos.model.TassaTrimestrale;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "SpeseController", value = "/SpeseController")
public class SpeseController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        // conferma pagamento
        if ("confirm".equals(action)) {

            long idPagamento;
            try {
                idPagamento = Long.parseLong(request.getParameter("idPagamento"));
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/SpeseController");
                return;
            }

            if (idPagamento <= 0) {
                response.sendRedirect(request.getContextPath() + "/SpeseController");
                return;
            }

            Pagamento pagamento = new PagamentoDAO().getPagamentoById(idPagamento);

            // pagamento non trovato
            if (pagamento == null) {
                response.sendRedirect(request.getContextPath() + "/SpeseController");
                return;
            }

            if (pagamento.getIdUtente() != utente.getIdUtente()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            request.setAttribute("pagamento", pagamento);
            request.getRequestDispatcher("/pagaSpesa.jsp").forward(request, response);
            return;
        }

        // sezione spese
        TassaDAO tassaDAO = new TassaDAO();
        PagamentoDAO pagamentoDAO = new PagamentoDAO();

        List<TassaTrimestrale> tasse =
                tassaDAO.getTasseByUtente(utente.getIdUtente());

        List<Pagamento> pagamenti =
                pagamentoDAO.getPagamentiByUtente(utente.getIdUtente());

        // Mappa: ID_Tassa → Pagamento
        Map<Long, Pagamento> pagamentoByTassa = new HashMap<>();
        for (Pagamento p : pagamenti) {
            pagamentoByTassa.put(p.getIdTassa(), p);
        }

        for (TassaTrimestrale t : tasse) {

            Pagamento p = pagamentoByTassa.get(t.getIdTassa());

            if (p != null) {
                t.setIdPagamento(p.getIdPagamento());
                t.setPagata(p.getDataPagamento() != null);
                t.setHasRicevuta(p.getDataPagamento() != null);
            } else {
                t.setPagata(false);
                t.setHasRicevuta(false);
            }
        }

        request.setAttribute("tasse", tasse);
        request.getRequestDispatcher("/spese.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("startPay".equals(action)) {

            long idTassa = Long.parseLong(request.getParameter("idTassa"));

            PagamentoDAO pagamentoDAO = new PagamentoDAO();
            long idPagamento =
                    pagamentoDAO.creaPagamentoDaTassa(idTassa, utente.getIdUtente());

            if (idPagamento <= 0) {
                response.sendRedirect(
                        request.getContextPath() + "/SpeseController?error=pagamento"
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/SpeseController?action=confirm&idPagamento=" + idPagamento
            );
        }

        // conferma pagamento
        if ("pay".equals(action)) {

            long idPagamento = Long.parseLong(request.getParameter("idPagamento"));
            String metodo = request.getParameter("metodo");

            PagamentoDAO pagamentoDAO = new PagamentoDAO();
            pagamentoDAO.registraPagamentoOnline(idPagamento, metodo);
            response.sendRedirect(request.getContextPath() + "/SpeseController");
        }
    }
}
