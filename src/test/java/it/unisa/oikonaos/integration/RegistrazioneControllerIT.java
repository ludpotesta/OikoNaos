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
import org.mockito.MockedStatic;
import util.database;

import javax.naming.*;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistrazioneControllerIT {
    private static void initJndiWithDataSource(DataSource ds) throws Exception {
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InMemoryInitialContextFactory.class.getName());

        InitialContext ic = new InitialContext();
        ic.rebind("java:comp/env/jdbc/OikoNaosDB", ds);
        ic.rebind("java:/comp/env/jdbc/OikoNaosDB", ds);
    }

    public static class InMemoryInitialContextFactory implements InitialContextFactory {
        private static final InMemoryContext CTX = new InMemoryContext();

        @Override
        public Context getInitialContext(Hashtable<?, ?> environment) {
            return CTX;
        }
    }

    public static class InMemoryContext implements Context {
        private final ConcurrentHashMap<String, Object> bindings = new ConcurrentHashMap<>();

        @Override
        public Object lookup(String name) throws NamingException {
            Object obj = bindings.get(name);
            if (obj == null) throw new NameNotFoundException(name);
            return obj;
        }

        @Override
        public Object lookup(Name name) throws NamingException {
            return lookup(name.toString());
        }

        @Override
        public void bind(String name, Object obj) throws NamingException {
            if (bindings.putIfAbsent(name, obj) != null) {
                throw new NameAlreadyBoundException(name);
            }
        }

        @Override
        public void bind(Name name, Object obj) throws NamingException {
            bind(name.toString(), obj);
        }

        @Override
        public void rebind(String name, Object obj) {
            bindings.put(name, obj);
        }

        @Override
        public void rebind(Name name, Object obj) {
            rebind(name.toString(), obj);
        }

        @Override
        public void unbind(String name) {
            bindings.remove(name);
        }

        @Override
        public void unbind(Name name) {
            unbind(name.toString());
        }

        @Override public void close() {}

        @Override public void rename(String oldName, String newName) { throw new UnsupportedOperationException(); }
        @Override public void rename(Name oldName, Name newName) { throw new UnsupportedOperationException(); }

        @Override public NamingEnumeration<NameClassPair> list(String name) { throw new UnsupportedOperationException(); }
        @Override public NamingEnumeration<NameClassPair> list(Name name) { throw new UnsupportedOperationException(); }

        @Override public NamingEnumeration<Binding> listBindings(String name) { throw new UnsupportedOperationException(); }
        @Override public NamingEnumeration<Binding> listBindings(Name name) { throw new UnsupportedOperationException(); }

        @Override public void destroySubcontext(String name) { throw new UnsupportedOperationException(); }
        @Override public void destroySubcontext(Name name) { throw new UnsupportedOperationException(); }

        @Override public Context createSubcontext(String name) { throw new UnsupportedOperationException(); }
        @Override public Context createSubcontext(Name name) { throw new UnsupportedOperationException(); }

        @Override public Object lookupLink(String name) { throw new UnsupportedOperationException(); }
        @Override public Object lookupLink(Name name) { throw new UnsupportedOperationException(); }

        @Override public NameParser getNameParser(String name) { throw new UnsupportedOperationException(); }
        @Override public NameParser getNameParser(Name name) { throw new UnsupportedOperationException(); }

        @Override public Name composeName(Name name, Name prefix) { throw new UnsupportedOperationException(); }
        @Override public String composeName(String name, String prefix) { throw new UnsupportedOperationException(); }

        @Override public Object addToEnvironment(String propName, Object propVal) { return null; }
        @Override public Object removeFromEnvironment(String propName) { return null; }
        @Override public Hashtable<?, ?> getEnvironment() { return new Hashtable<>(); }
        @Override public String getNameInNamespace() { return ""; }
    }

    private static void stubValidRegistrationParams(HttpServletRequest request) {
        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@test.it");
        when(request.getParameter("telefono")).thenReturn("3331234567");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("Zxcvbn9!");
        when(request.getParameter("codiceID")).thenReturn("ABC123");
    }

    @Test
    void registrazioneValida() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        stubValidRegistrationParams(request);
        when(request.getSession(true)).thenReturn(session);

        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
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

            try (MockedStatic<database> dbMock = mockStatic(database.class)) {
                dbMock.when(database::getConnection).thenReturn(con);

                controller.service(request, response);
            }

            verify(con).setAutoCommit(false);
            verify(con).commit();

            verify(session).setAttribute("utente", utenteLoggato);
            verify(response).sendRedirect("/OikoNaos_war_exploded/home.jsp?msg=registrato");
        }
    }

    @Test
    void campiObbligatoriVuoti() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        when(request.getParameter("nome")).thenReturn("  ");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@test.it");
        when(request.getParameter("telefono")).thenReturn("3331234567");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("Zxcvbn9!");
        when(request.getParameter("codiceID")).thenReturn("ABC123");

        controller.service(request, response);

        verify(response).sendRedirect("/OikoNaos_war_exploded/register.jsp?error=campi");
    }

    @Test
    void codiceIdentificativoNonValido() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        stubValidRegistrationParams(request);

        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
        initJndiWithDataSource(ds);

        try (MockedConstruction<CredenzialiDAO> credMocked =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.usernameEsistente("mario")).thenReturn(false);
                     });
             MockedConstruction<UserDAO> userMocked =
                     mockConstruction(UserDAO.class);
             MockedConstruction<CodiceIdentificativoDAO> codiceMocked =
                     mockConstruction(CodiceIdentificativoDAO.class, (mock, ctx) -> {
                         when(mock.getCodiceValidoForUpdate(con, "ABC123")).thenReturn(null);
                     })) {

            try (MockedStatic<database> dbMock = mockStatic(database.class)) {
                dbMock.when(database::getConnection).thenReturn(con);

                controller.service(request, response);
            }

            UserDAO userDao = userMocked.constructed().get(0);
            verify(userDao, never()).registerUser(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
            verify(con, never()).commit();

            verify(response).sendRedirect("/OikoNaos_war_exploded/register.jsp?error=codice");
        }
    }

    @Test
    void usernameGiaEsistente() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        stubValidRegistrationParams(request);

        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
        initJndiWithDataSource(ds);

        try (MockedConstruction<CredenzialiDAO> credMocked =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.usernameEsistente("mario")).thenReturn(true);
                     });
             MockedConstruction<UserDAO> userMocked =
                     mockConstruction(UserDAO.class);
             MockedConstruction<CodiceIdentificativoDAO> codiceMocked =
                     mockConstruction(CodiceIdentificativoDAO.class)) {

            try (MockedStatic<database> dbMock = mockStatic(database.class)) {
                dbMock.when(database::getConnection).thenReturn(con);

                controller.service(request, response);
            }

            verify(con, never()).commit();
            verify(response).sendRedirect("/OikoNaos_war_exploded/register.jsp?error=username");

            UserDAO userDao = userMocked.constructed().get(0);
            verify(userDao, never()).registerUser(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
        }
    }

    @Test
    void emailGiaEsistente() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        stubValidRegistrationParams(request);

        DataSource ds = mock(DataSource.class);
        Connection con = mock(Connection.class);
        when(ds.getConnection()).thenReturn(con);
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
                         when(mock.registerUser(any(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                                 .thenThrow(new SQLException("Duplicate entry for Email"));
                     })) {

            try (MockedStatic<database> dbMock = mockStatic(database.class)) {
                dbMock.when(database::getConnection).thenReturn(con);

                controller.service(request, response);
            }

            String expected = "/OikoNaos_war_exploded/register.jsp?error=" +
                    URLEncoder.encode("Errore durante la registrazione", StandardCharsets.UTF_8);
            verify(response).sendRedirect(expected);

            verify(con, never()).commit();
        }
    }

    @Test
    void passwordNonValida() throws Exception {
        RegistrazioneController controller = new RegistrazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        when(request.getParameter("nome")).thenReturn("Mario");
        when(request.getParameter("cognome")).thenReturn("Rossi");
        when(request.getParameter("email")).thenReturn("mario.rossi@test.it");
        when(request.getParameter("telefono")).thenReturn("3331234567");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("short");
        when(request.getParameter("codiceID")).thenReturn("ABC123");

        controller.service(request, response);

        String msg = URLEncoder.encode("deve contenere almeno 8 caratteri", StandardCharsets.UTF_8);
        String expected = "/OikoNaos_war_exploded/register.jsp?error=pwd&msg=" + msg;

        verify(response).sendRedirect(expected);
    }
}