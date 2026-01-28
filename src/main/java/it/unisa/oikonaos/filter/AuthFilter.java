package it.unisa.oikonaos.filter;

import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();
        System.out.println("[AuthFilter] servletPath=" + path
                + " | uri=" + request.getRequestURI());

        HttpSession session = request.getSession(false);

        // 1. RISORSE E PAGINE PUBBLICHE
        if (path.equals("/login.jsp")
                || path.equals("/register.jsp")
                || path.equals("/index.jsp")
                || path.equals("/passwordDimenticata.jsp")
                || path.equals("/resetPassword.jsp")
                || path.equals("/forgot-password.jsp")
                || path.equals("/recupera-password.jsp")
                || path.equals("/recupera-password")
                || path.equals("/modifica-password.jsp")
                || path.equals("/modifica-password")
                || path.startsWith("/RichiestaResetPasswordController")
                || path.startsWith("/ResetPasswordController")
                || path.equals("/AutenticazioneController")
                || path.equals("/RegistrazioneController")
                || path.equals("/LogoutController")
                || path.startsWith("/BachecaEventiController")
                || path.startsWith("/assets/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".svg")
                || path.endsWith(".ico")) {

            chain.doFilter(req, res);
            return;
        }

        // CONTROLLO AUTENTICAZIONE
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // CONTROLLO AUTORIZZAZIONE RUOLI
        Utente utente = (Utente) session.getAttribute("utente");

        if (path.startsWith("/supervisore") || path.contains("Supervisore")) {
            if (!"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
                response.sendRedirect(request.getContextPath() + "/home.jsp?error=ruolo");
                return;
            }
        }

        // ACCESSO CONSENTITO
        chain.doFilter(req, res);
    }
}
