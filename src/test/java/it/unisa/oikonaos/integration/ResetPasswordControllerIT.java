package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.ResetPasswordController;
import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.TokenResetPasswordDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ResetPasswordControllerIT {

    private static void stubBase(HttpServletRequest request) {
        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
    }

    private static void stubValidParams(HttpServletRequest request) {
        stubBase(request);
        when(request.getParameter("nuovaPassword")).thenReturn("NuovaPwd9!");
        when(request.getParameter("confermaPassword")).thenReturn("NuovaPwd9!");
    }

    @Test
    void IT_MOD_01_resetPasswordValido() throws Exception {
        ResetPasswordController controller = new ResetPasswordController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        stubValidParams(request);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("resetToken")).thenReturn("TOK123");

        Long idUtente = 10L;

        try (MockedConstruction<TokenResetPasswordDAO> tokenDaoMocked =
                     mockConstruction(TokenResetPasswordDAO.class, (mock, ctx) -> {
                         when(mock.getIdUtenteByToken("TOK123")).thenReturn(idUtente);
                     });
             MockedConstruction<CredenzialiDAO> credDaoMocked =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.updatePassword(eq(idUtente), anyString())).thenReturn(true);
                     })) {

            controller.service(request, response);

            CredenzialiDAO credDao = credDaoMocked.constructed().get(0);
            verify(credDao).updatePassword(eq(idUtente), anyString());

            TokenResetPasswordDAO tokenDao = tokenDaoMocked.constructed().get(0);
            verify(tokenDao).invalidateToken("TOK123");

            verify(session).invalidate();

            verify(response).sendRedirect("/OikoNaos_war_exploded/login.jsp?success=reset");
        }
    }

    @Test
    void IT_MOD_02_tokenNonValido_oScaduto() throws Exception {
        ResetPasswordController controller = new ResetPasswordController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        stubValidParams(request);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("resetToken")).thenReturn("TOK123");

        try (MockedConstruction<TokenResetPasswordDAO> tokenDaoMocked =
                     mockConstruction(TokenResetPasswordDAO.class, (mock, ctx) -> {
                         when(mock.getIdUtenteByToken("TOK123")).thenReturn(null);
                     });
             MockedConstruction<CredenzialiDAO> credDaoMocked =
                     mockConstruction(CredenzialiDAO.class)) {

            controller.service(request, response);
            assertTrue(credDaoMocked.constructed().isEmpty(), "CredenzialiDAO non dovrebbe essere istanziato in caso di token non valido");

            if (!tokenDaoMocked.constructed().isEmpty()) {
                TokenResetPasswordDAO tokenDao = tokenDaoMocked.constructed().get(0);
                verify(tokenDao, never()).invalidateToken(anyString());
            }

            verify(session, never()).invalidate();

            verify(response).sendRedirect("/OikoNaos_war_exploded/resetPassword.jsp?error=token");
        }
    }

    @Test
    void IT_MOD_03_nuovaPasswordNonValida_campiVuoti() throws Exception {
        ResetPasswordController controller = new ResetPasswordController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        stubBase(request);
        when(request.getParameter("nuovaPassword")).thenReturn("   ");
        when(request.getParameter("confermaPassword")).thenReturn("   ");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("resetToken")).thenReturn("TOK123");

        try (MockedConstruction<TokenResetPasswordDAO> tokenDaoMocked =
                     mockConstruction(TokenResetPasswordDAO.class);
             MockedConstruction<CredenzialiDAO> credDaoMocked =
                     mockConstruction(CredenzialiDAO.class)) {

            controller.service(request, response);

            assertTrue(credDaoMocked.constructed().isEmpty(), "CredenzialiDAO non dovrebbe essere istanziato quando i campi sono vuoti");
            assertTrue(tokenDaoMocked.constructed().isEmpty(), "TokenResetPasswordDAO non dovrebbe essere istanziato quando i campi sono vuoti");

            verify(session, never()).invalidate();
            verify(response).sendRedirect("/OikoNaos_war_exploded/resetPassword.jsp?error=campi");
        }
    }

    @Test
    void IT_MOD_04_confermaPasswordNonCoincidente() throws Exception {
        ResetPasswordController controller = new ResetPasswordController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        stubBase(request);
        when(request.getParameter("nuovaPassword")).thenReturn("NuovaPwd9!");
        when(request.getParameter("confermaPassword")).thenReturn("DiversaPwd9!");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("resetToken")).thenReturn("TOK123");

        try (MockedConstruction<TokenResetPasswordDAO> tokenDaoMocked =
                     mockConstruction(TokenResetPasswordDAO.class);
             MockedConstruction<CredenzialiDAO> credDaoMocked =
                     mockConstruction(CredenzialiDAO.class)) {

            controller.service(request, response);
            
            assertTrue(credDaoMocked.constructed().isEmpty(), "CredenzialiDAO non dovrebbe essere istanziato quando le password non coincidono");
            assertTrue(tokenDaoMocked.constructed().isEmpty(), "TokenResetPasswordDAO non dovrebbe essere istanziato quando le password non coincidono");

            verify(session, never()).invalidate();
            verify(response).sendRedirect("/OikoNaos_war_exploded/resetPassword.jsp?error=match");
        }
    }
}