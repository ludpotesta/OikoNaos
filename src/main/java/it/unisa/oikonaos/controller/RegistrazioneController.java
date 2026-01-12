package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.*;
import it.unisa.oikonaos.model.CodiceIdentificativo;
import it.unisa.oikonaos.model.Utente;
import util.database;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;

@WebServlet(name = "RegistrazioneController", value = "/RegistrazioneController")
public class RegistrazioneController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String telefono = request.getParameter("telefono");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String codice = request.getParameter("codiceID");

        if (nome == null || cognome == null || email == null ||
                telefono == null || username == null ||
                password == null || codice == null ||
                nome.isBlank() || cognome.isBlank() ||
                username.isBlank() || password.isBlank() ||
                codice.isBlank()) {

            response.sendRedirect(request.getContextPath() + "/register.jsp?error=campi");
            return;
        }

        try (Connection con = database.getConnection()) {

            con.setAutoCommit(false);

            CredenzialiDAO credenzialiDAO = new CredenzialiDAO();
            UserDAO userDAO = new UserDAO();
            CodiceIdentificativoDAO codiceDAO = new CodiceIdentificativoDAO();

            // Username già esistente
            if (credenzialiDAO.usernameEsistente(username)) {
                response.sendRedirect(request.getContextPath() + "/register.jsp?error=username");
                return;
            }

            // Verifica codice (CON LOCK)
            CodiceIdentificativo codiceValido =
                    codiceDAO.getCodiceValidoForUpdate(con, codice);

            if (codiceValido == null) {
                response.sendRedirect(request.getContextPath() + "/register.jsp?error=codice");
                return;
            }

            // Registrazione utente (stessa connessione!)
            long idUtente = userDAO.registerUser(
                    con, nome, cognome, email, telefono,
                    username, password,
                    codiceValido.getIdComunita()
            );

            // Consumo codice
            codiceDAO.marcaComeUsato(con, codice, idUtente);

            con.commit();

            // Login automatico
            Utente utente = userDAO.login(username, password);

            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);

            response.sendRedirect(request.getContextPath() + "/home.jsp?msg=registrato");

        } catch (Exception e) {
            e.printStackTrace();
            String msg = URLEncoder.encode("Errore durante la registrazione", "UTF-8");
            response.sendRedirect(request.getContextPath() + "/register.jsp?error=" + msg);
        }
    }
}
