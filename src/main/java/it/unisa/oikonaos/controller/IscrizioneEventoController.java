package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.EventoDAO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "IscrizioneEventoController", value = "/IscrizioneEventoController")
public class IscrizioneEventoController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        long idEvento = Long.parseLong(request.getParameter("idEvento"));

        try {
            EventoDAO dao = new EventoDAO();
            dao.iscriviUtenteEvento(u.getIdUtente(), idEvento);

            response.sendRedirect(
                    request.getContextPath() + "/confermaIscrizioneEvento.jsp"
            );

        } catch (IllegalStateException e) {
            response.sendRedirect(
                    request.getContextPath() + "/BachecaEventiController?error=posti"
            );
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/BachecaEventiController?error=generico"
            );
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

}
