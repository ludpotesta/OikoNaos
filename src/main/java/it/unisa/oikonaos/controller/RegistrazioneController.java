package it.unisa.oikonaos.controller;

import it.unisa.oikonaos.dao.*;
import it.unisa.oikonaos.model.CodiceIdentificativo;
import it.unisa.oikonaos.model.Utente;
import util.PasswordValidator;
import java.nio.charset.StandardCharsets;
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

        // Controllo requisiti password (RAD UC06)
        String pwdError = PasswordValidator.validate(password, nome, cognome, username);
        if (pwdError != null) {
            String enc = URLEncoder.encode(pwdError, StandardCharsets.UTF_8);
            response.sendRedirect(
                    request.getContextPath() + "/register.jsp?error=pwd&msg=" + enc
            );
            return;
        }

        try (Connection con = database.getConnection()) {
            System.out.println("DB usato = " + con.getCatalog());

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

            long idUtente = userDAO.registerUser(
                    con, nome, cognome, email, telefono,
                    username, password,
                    codiceValido.getIdComunita()
            );

            codiceDAO.marcaComeUsato(con, codice, idUtente);

            con.commit();

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

    @WebServlet(name = "ResetPasswordController", value = "/ResetPasswordController")
    public static class RichiestaResetPasswordController extends HttpServlet {

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            String username = request.getParameter("username");

            if (username == null || username.isBlank()) {
                response.sendRedirect("forgot-password.jsp?error=campi");
                return;
            }

            try {
                CredenzialiDAO credDAO = new CredenzialiDAO();
                Long idUtente = credDAO.getIdUtenteByUsername(username);

                // 🔒 Non riveliamo se l’utente esiste o meno
                if (idUtente == null) {
                    response.sendRedirect("forgot-password.jsp?success=ok");
                    return;
                }

                TokenResetPasswordDAO tokenDAO = new TokenResetPasswordDAO();
                String token = tokenDAO.createToken(idUtente);

                // Per ora mostriamo il link (NO email)
                String link = request.getContextPath()
                        + "/reset-password.jsp?token=" + URLEncoder.encode(token, "UTF-8");

                response.sendRedirect("forgot-password.jsp?success=ok&link=" +
                        URLEncoder.encode(link, "UTF-8"));

            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("forgot-password.jsp?error=generico");
            }
        }
    }
}
