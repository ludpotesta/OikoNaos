package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.AutenticazioneController;
import it.unisa.oikonaos.controller.LogoutController;
import it.unisa.oikonaos.dao.CredenzialiDAO;
import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.filter.AuthFilter;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mindrot.jbcrypt.BCrypt;

import static org.mockito.Mockito.*;

public class AutenticazioneBloccoAIT {
    
    @Test
    void IT_AUTH_01_loginValido_creaSessione() throws Exception {

        AutenticazioneController controller = new AutenticazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("Password123!");
        when(request.getSession(true)).thenReturn(session);

        Long idUtente = 1L;
        String hash = BCrypt.hashpw("Password123!", BCrypt.gensalt());

        Utente u = new Utente();
        u.setIdUtente(idUtente);
        u.setRuolo("COINQUILINO");

        try (MockedConstruction<CredenzialiDAO> credMock =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.getIdUtenteByUsername("mario")).thenReturn(idUtente);
                         when(mock.getPasswordHashByUtente(idUtente)).thenReturn(hash);
                     });
             MockedConstruction<UserDAO> userMock =
                     mockConstruction(UserDAO.class, (mock, ctx) -> {
                         when(mock.getUtenteById(idUtente)).thenReturn(u);
                     })) {

            controller.service(request, response);

            verify(request).getSession(true);
            verify(session).setAttribute(eq("utente"), any(Utente.class));
            verify(response).sendRedirect("/OikoNaos_war_exploded/home.jsp");
        }
    }

    @Test
    void IT_AUTH_02_passwordErrata() throws Exception {

        AutenticazioneController controller = new AutenticazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("username")).thenReturn("mario");
        when(request.getParameter("password")).thenReturn("WRONG");

        Long idUtente = 1L;
        String hash = BCrypt.hashpw("Password123!", BCrypt.gensalt());

        try (MockedConstruction<CredenzialiDAO> credMock =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.getIdUtenteByUsername("mario")).thenReturn(idUtente);
                         when(mock.getPasswordHashByUtente(idUtente)).thenReturn(hash);
                     })) {

            controller.service(request, response);

            verify(request, never()).getSession(true);
            verify(response)
                    .sendRedirect("/OikoNaos_war_exploded/login.jsp?error=credenziali");
        }
    }

    @Test
    void IT_AUTH_03_usernameInesistente() throws Exception {

        AutenticazioneController controller = new AutenticazioneController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("username")).thenReturn("ghost");
        when(request.getParameter("password")).thenReturn("qualsiasi");

        try (MockedConstruction<CredenzialiDAO> credMock =
                     mockConstruction(CredenzialiDAO.class, (mock, ctx) -> {
                         when(mock.getIdUtenteByUsername("ghost")).thenReturn(null);
                     })) {

            controller.service(request, response);

            verify(request, never()).getSession(true);
            verify(response)
                    .sendRedirect("/OikoNaos_war_exploded/login.jsp?error=credenziali");
        }
    }

    @Test
    void IT_AUTH_04_logoutInvalidaSessione() throws Exception {

        LogoutController controller = new LogoutController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getSession(false)).thenReturn(session);

        controller.service(request, response);

        verify(session).invalidate();
        verify(response).sendRedirect("/OikoNaos_war_exploded/login.jsp");
    }

    @Test
    void IT_AUTH_05_coinquilinoSupervisoreNegato() throws Exception {

        AuthFilter filter = new AuthFilter();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(1L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        when(request.getServletPath()).thenReturn("/supervisore/home.jsp");
        when(request.getRequestURI())
                .thenReturn("/OikoNaos_war_exploded/supervisore/home.jsp");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        filter.doFilter(
                (ServletRequest) request,
                (ServletResponse) response,
                chain
        );

        verify(response)
                .sendRedirect("/OikoNaos_war_exploded/home.jsp?error=ruolo");
        verify(chain, never()).doFilter(any(), any());
    }
}