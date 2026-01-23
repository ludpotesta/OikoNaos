package it.unisa.oikonaos.filter;

import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebFilter("/*")
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String ctx = request.getContextPath();
        String uri = request.getRequestURI();
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        // Utente NON loggato
        if (utente == null) {
            chain.doFilter(req, res);
            return;
        }

        // SUPERVISORE che entra nella home normale
        if (uri.endsWith(ctx + "/home.jsp") && utente.getRuolo().equals("SUPERVISORE")) {

            response.sendRedirect(ctx + "/supervisore/home.jsp");
            return;
        }


        chain.doFilter(req, res);
    }
}

