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
        HttpSession session = request.getSession(false);

        System.out.println("Request: " + path);

        if (path.equals("/login.jsp")
                || path.equals("/register.jsp")
                || path.equals("/index.jsp")
                || path.startsWith("/assets/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.equals("/AutenticazioneController")
                || path.equals("/RegistrazioneController")
                || path.equals("/LogoutController")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".svg")
                || path.endsWith(".ico")) {

            chain.doFilter(req, res);
            return;
        }

        //Controllo autenticazione
        if (session == null || session.getAttribute("utente") == null) {
            System.out.println("Accesso BLOCCATO → non autenticato");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        //Controllo autorizzazione
        Utente utente = (Utente) session.getAttribute("utente");

        // area supervisore
        if (path.startsWith("/supervisore")) {
            if (!"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
                System.out.println("Accesso BLOCCATO → ruolo non autorizzato");
                response.sendRedirect(request.getContextPath() + "/home.jsp?error=ruolo");
                return;
            }
        }

        //accesso consentito
        System.out.println("Accesso CONSENTITO");
        chain.doFilter(req, res);
    }
}
