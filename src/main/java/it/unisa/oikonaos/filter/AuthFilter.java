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

        // 1. ESCLUSIONI: Risorse e pagine sempre accessibili (anche non loggati)
        if (path.equals("/login.jsp")
                || path.equals("/register.jsp")
                || path.equals("/index.jsp")
                || path.equals("/passwordDimenticata.jsp")
                || path.equals("/resetPassword.jsp")
                || path.equals("/ResetPasswordController")
                || path.equals("/RichiestaResetPasswordController")
                || path.equals("/ConfermaResetPasswordController")
                || path.equals("/AutenticazioneController")
                || path.equals("/RegistrazioneController")
                || path.equals("/LogoutController")
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

        // 2. CONTROLLO AUTENTICAZIONE: Se non sei loggato, vai al login
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=session");
            return;
        }

        // 3. CONTROLLO AUTORIZZAZIONE: Gestione dei ruoli
        Utente utente = (Utente) session.getAttribute("utente");

        // Protezione specifica per le cartelle o i controller "Supervisore"
        if (path.startsWith("/supervisore") || path.contains("Supervisore")) {
            if (!"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
                // Se non sei supervisore, ti mando alla home con l'errore
                // La home.jsp non ha più il redirect interno, quindi il loop si ferma qui.
                System.out.println("[AuthFilter] BLOCCO -> redirect login (non loggato) path=" + path);

                response.sendRedirect(request.getContextPath() + "/home.jsp?error=ruolo");
                return;
            }
        }

        // 4. ACCESSO CONSENTITO: Prosegui verso la risorsa richiesta
        chain.doFilter(req, res);
    }
}