package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.RegistrazioneController;
import it.unisa.oikonaos.dao.CodiceIdentificativoDAO;
import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.CodiceIdentificativo;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistrazioneControllerIT {

    /**
     * RegistrazioneController usa util.database (JNDI lookup java:comp/env/jdbc/OikoNaosDB).
     * Per farlo funzionare nei test senza Tomcat, inizializziamo un contesto JNDI in-memory
     * (Apache Naming) e bindiamo un DataSource mock.
     */
    private static void initJndiWithDataSource(DataSource ds) throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

        InitialContext ic = new InitialContext();

        // crea sotto-contesti se mancanti
        try { ic.createSubcontext("java:"); } catch (Exception ignored) {}
        try { ic.createSubcontext("java:comp"); } catch (Exception ignored) {}
        try { ic.createSubcontext("java:comp/env"); } catch (Exception ignored) {}
        try { ic.createSubcontext("java:comp/env/jdbc"); } catch (Exception ignored) {}

        // rebind DS
        ic.rebind("java:comp/env/jdbc/OikoNaosDB", ds);
    }

    private static void stubValidRegistrationParams(HttpServletRequest request) {
        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war");

        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@test.it");
        when(request.getParameter("telefono")).thenReturn("3331234567");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("Zxcvbn9!"); // valida per PasswordValidator
        when(request.getParameter("codiceID")).thenReturn("ABC123");
    }

    // IT-REG-01 — Registrazione valida
    @Test
    void registrazioneValida() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        stubValidRegistrationParams(request);
        when(request.getSession(true)).thenReturn(session);

        // JNDI DS -> conn mock
        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
        when(con.getCatalog()).thenReturn("oikonaos");
        initJndiWithDataSource(ds);

        long newId = 77L;

        CodiceIdentificativo codiceOk = new CodiceIdentificativo();
        codiceOk.setCodice("ABC123");
        codiceOk.setStato("ATTIVO");

        Utente utenteLoggato = new Utente();
        utenteLoggato.setIdUtente(newId);
        utenteLoggato.setRuolo("COINQUILINO");

        try (MockedConstruction<CredenzialiDAO> credMocked =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.usernameEsistente("mario")).thenReturn(false);
                     });
             MockedConstruction<UserDAO> userMocked =
                     mockConstruction(UserDAO.class, (mock, ctx) -> {
                         when(mock.registerUser(
                                 eq(con),
                                 eq("Mario"), eq("Rossi"),
                                 eq("mario.rossi@test.it"),
                                 eq("3331234567"),
                                 eq("mario"),
                                 eq("Zxcvbn9!")
                         )).thenReturn(newId);

                         when(mock.login("mario", "Zxcvbn9!")).thenReturn(utenteLoggato);
                     });
             MockedConstruction<CodiceIdentificativoDAO> codiceMocked =
                     mockConstruction(CodiceIdentificativoDAO.class, (mock, ctx) -> {
                         when(mock.getCodiceValidoForUpdate(con, "ABC123")).thenReturn(codiceOk);
                         when(mock.marcaComeUsato(con, "ABC123", newId)).thenReturn(true);
                     })) {

            controller.service(request, response);

            verify(con).setAutoCommit(false);
            verify(con).commit();

            verify(session).setAttribute("utente", utenteLoggato);
            verify(response).sendRedirect("/OikoNaos_war/home.jsp?msg=registrato");
        }
    }

    // IT-REG-02 — Campi obbligatori vuoti
    @Test
    void campiObbligatoriVuoti() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war");

        // nome blank => trigger error=campi
        when(request.getParameter("nome")).thenReturn("  ");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@test.it");
        when(request.getParameter("telefono")).thenReturn("3331234567");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("Zxcvbn9!");
        when(request.getParameter("codiceID")).thenReturn("ABC123");

        controller.service(request, response);

        verify(response).sendRedirect("/OikoNaos_war/register.jsp?error=campi");
    }

    // IT-REG-03 — Codice identificativo non valido
    @Test
    void codiceIdentificativoNonValido() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        stubValidRegistrationParams(request);

        // JNDI DS -> conn mock
        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
        when(con.getCatalog()).thenReturn("oikonaos");
        initJndiWithDataSource(ds);

        try (MockedConstruction<CredenzialiDAO> credMocked =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.usernameEsistente("mario")).thenReturn(false);
                     });
             MockedConstruction<UserDAO> userMocked =
                     mockConstruction(UserDAO.class);
             MockedConstruction<CodiceIdentificativoDAO> codiceMocked =
                     mockConstruction(CodiceIdentificativoDAO.class, (mock, ctx) -> {
                         // codice NON valido
                         when(mock.getCodiceValidoForUpdate(con, "ABC123")).thenReturn(null);
                     })) {

            controller.service(request, response);

            // deve fermarsi prima di registerUser
            UserDAO userDao = userMocked.constructed().get(0);
            verify(userDao, never()).registerUser(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
            verify(con, never()).commit();

            verify(response).sendRedirect("/OikoNaos_war/register.jsp?error=codice");
        }
    }

    // IT-REG-04 — Username già esistente
    @Test
    void usernameGiaEsistente() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        stubValidRegistrationParams(request);

        // JNDI DS -> conn mock (serve perché il controller apre la connessione prima del check username)
        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
        when(con.getCatalog()).thenReturn("oikonaos");
        initJndiWithDataSource(ds);

        try (MockedConstruction<CredenzialiDAO> credMocked =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.usernameEsistente("mario")).thenReturn(true);
                     });
             MockedConstruction<UserDAO> userMocked =
                     mockConstruction(UserDAO.class);
             MockedConstruction<CodiceIdentificativoDAO> codiceMocked =
                     mockConstruction(CodiceIdentificativoDAO.class)) {

            controller.service(request, response);

            // stop subito
            verify(con, never()).commit();
            verify(response).sendRedirect("/OikoNaos_war/register.jsp?error=username");

            // non deve mai chiamare registerUser
            UserDAO userDao = userMocked.constructed().get(0);
            verify(userDao, never()).registerUser(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        }
    }

    // IT-REG-05 — Email già esistente
    // Nota: nel codice attuale NON c’è un controllo esplicito sull’email (es. emailEsistente()).
    // Quindi simuliamo lo scenario come “vincolo DB/errore in insertUtente” => eccezione => redirect error generico.
    @Test
    void emailGiaEsistente() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        stubValidRegistrationParams(request);

        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
        when(con.getCatalog()).thenReturn("oikonaos");
        initJndiWithDataSource(ds);

        CodiceIdentificativo codiceOk = new CodiceIdentificativo();
        codiceOk.setCodice("ABC123");
        codiceOk.setStato("ATTIVO");

        try (MockedConstruction<CredenzialiDAO> credMocked =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.usernameEsistente("mario")).thenReturn(false);
                     });
             MockedConstruction<CodiceIdentificativoDAO> codiceMocked =
                     mockConstruction(CodiceIdentificativoDAO.class, (mock, ctx) -> {
                         when(mock.getCodiceValidoForUpdate(con, "ABC123")).thenReturn(codiceOk);
                     });
             MockedConstruction<UserDAO> userMocked =
                     mockConstruction(UserDAO.class, (mock, ctx) -> {
                         // Simula “email duplicata” -> eccezione DB durante registerUser
                         when(mock.registerUser(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                                 .thenThrow(new SQLException("Duplicate entry for Email"));
                     })) {

            controller.service(request, response);

            // redirect error generico (come da catch)
            String expected = "/OikoNaos_war/register.jsp?error=" +
                    URLEncoder.encode("Errore durante la registrazione", StandardCharsets.UTF_8);
            verify(response).sendRedirect(expected);

            verify(con, never()).commit();
        }
    }

    // IT-REG-06 — Password non valida
    @Test
    void passwordNonValida() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war");

        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@test.it");
        when(request.getParameter("telefono")).thenReturn("3331234567");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("short"); // NON valida: < 8
        when(request.getParameter("codiceID")).thenReturn("ABC123");

        controller.service(request, response);

        // PasswordValidator -> "deve contenere almeno 8 caratteri"
        String msg = URLEncoder.encode("deve contenere almeno 8 caratteri", StandardCharsets.UTF_8);
        String expected = "/OikoNaos_war/register.jsp?error=pwd&msg=" + msg;

        verify(response).sendRedirect(expected);
    }
}