package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.RicevutaDAO;
import it.unisa.oikonaos.model.Ricevuta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/RicevutaController")
public class RicevutaController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        long idPagamento =
                Long.parseLong(request.getParameter("idPagamento"));

        RicevutaDAO ricevutaDAO = new RicevutaDAO();
        Optional<Ricevuta> ricevuta =
                ricevutaDAO.getRicevutaByPagamento(idPagamento);

        if (ricevuta.isEmpty()) {
            request.setAttribute("errore", "Ricevuta non trovata");
        } else {
            request.setAttribute("ricevuta", ricevuta.get());
        }

        request.getRequestDispatcher("/ricevuta.jsp")
                .forward(request, response);
    }
}

