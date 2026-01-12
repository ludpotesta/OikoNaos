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
import java.util.List;

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

        /* CONFERMA PAGAMENTO */
        if ("confirm".equals(action)) {
            long idPagamento =
                    Long.parseLong(request.getParameter("idPagamento"));

            Pagamento pagamento =
                    new PagamentoDAO().getPagamentoById(idPagamento);

            request.setAttribute("pagamento", pagamento);
            request.getRequestDispatcher("/pagaSpesa.jsp")
                    .forward(request, response);
            return;
        }

        /* LISTA TASSE (LE MIE SPESE)*/
        TassaDAO tassaDAO = new TassaDAO();
        List<TassaTrimestrale> tasse =
                tassaDAO.getTasseByUtente(utente.getIdUtente());

        request.setAttribute("tasse", tasse);
        request.getRequestDispatcher("/spese.jsp")
                .forward(request, response);
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

        // 1) Avvio pagamento: crea record pagamento e vai alla conferma
        if ("startPay".equals(action)) {

            long idTassa = Long.parseLong(request.getParameter("idTassa"));

            PagamentoDAO pagamentoDAO = new PagamentoDAO();
            long idPagamento = pagamentoDAO.creaPagamentoDaTassa(idTassa, utente.getIdUtente());

            if (idPagamento <= 0) {
                // fallimento: torna alla lista (puoi anche mettere ?error=1)
                response.sendRedirect(request.getContextPath() + "/SpeseController");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/SpeseController?action=confirm&idPagamento=" + idPagamento
            );
            return;
        }

        // 2) Conferma pagamento: aggiorna pagamento + ricevuta + tassa pagata
        if ("pay".equals(action)) {

            long idPagamento = Long.parseLong(request.getParameter("idPagamento"));
            String metodo = request.getParameter("metodo");

            PagamentoDAO pagamentoDAO = new PagamentoDAO();
            pagamentoDAO.registraPagamentoOnline(idPagamento, metodo);

            // ora marca la tassa come PAGATA (dopo conferma)
            Pagamento pag = pagamentoDAO.getPagamentoById(idPagamento);
            if (pag != null) {
                new TassaDAO().marcaComePagata(pag.getIdTassa());
            }

            response.sendRedirect(request.getContextPath() + "/SpeseController");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/SpeseController");
    }
}
