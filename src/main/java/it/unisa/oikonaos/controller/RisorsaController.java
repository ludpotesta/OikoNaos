package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.model.Utente;
import it.unisa.oikonaos.dao.RisorsaDAO;
import it.unisa.oikonaos.dao.RichiestaRisorsaDAO;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

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
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            RisorsaDAO risorsaDAO = new RisorsaDAO();
            RichiestaRisorsaDAO richiestaDAO = new RichiestaRisorsaDAO();

            // Test n.2 – consultazione
            request.setAttribute(
                    "risorseDisponibili",
                    risorsaDAO.findAllDisponibili()
            );

            request.setAttribute(
                    "richiesteAttive",
                    richiestaDAO.findByUtente(utente.getIdUtente())
            );

            RequestDispatcher rd =
                    request.getRequestDispatcher("/risorse.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("home.jsp?error=risorse");
        }
    }
}

