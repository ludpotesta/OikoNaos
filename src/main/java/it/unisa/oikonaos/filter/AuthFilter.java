package it.unisa.oikonaos.filter;

import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter{

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();
        HttpSession session = request.getSession(false);

        System.out.println("Request: " + path);

        //Risorse pubbliche / statiche
        if (path.contains("/index.jsp")
                || path.contains("/login.jsp")
                || path.contains("/register.jsp")
                || path.contains("/AutenticazioneController")
                || path.contains("/RegistrazioneController")
                || path.contains("/LogoutController")
                || path.contains("/assets/")
                || path.contains("/css/")
                || path.contains("/js/")
                || path.endsWith(".png")
                || path.endsWith(".jpg")
                || path.endsWith(".svg")
                || path.endsWith(".ico")) {

            chain.doFilter(req, res);
            return;
        }

        //Controllo autenticazione
        if (session == null || session.getAttribute("utente") == null) {
            System.out.println("Accesso BLOCCATO → redirect login");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        //CONTROLLO AUTORIZZAZIONE (RUOLI)
        Utente utente = (Utente) session.getAttribute("utente");

        // URL riservate ai supervisori
                if (path.contains("/supervisore") || path.contains("/supervisore")) {

                    if (!"SUPERVISORE".equalsIgnoreCase(utente.getRuolo())) {
                        System.out.println("Accesso BLOCCATO → area supervisore");
                        response.sendRedirect(request.getContextPath() + "/home.jsp?error=ruolo");
                        return;
                    }
                }

        System.out.println("Accesso CONSENTITO");
        chain.doFilter(req, res);
    }
}
