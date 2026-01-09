package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.PagamentoDAO;
import it.unisa.oikonaos.model.Pagamento;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
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

        PagamentoDAO pagamentoDAO = new PagamentoDAO();
        List<Pagamento> pagamenti = pagamentoDAO.getPagamentiByUtente(utente.getIdUtente());

        request.setAttribute("pagamenti", pagamenti);
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

        if ("pay".equals(action)) {
            long idPagamento = Long.parseLong(request.getParameter("idPagamento"));
            new PagamentoDAO().registraPagamentoOnline(idPagamento);
        }

        response.sendRedirect(request.getContextPath() + "/spese");
    }
}
