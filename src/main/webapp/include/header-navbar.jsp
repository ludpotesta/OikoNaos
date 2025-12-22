<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css" />

<header class="on-navbar">

    <div class="on-left">
        <a href="${pageContext.request.contextPath}/index.jsp"
           class="on-logo-link"
           aria-label="Vai alla home">
            <img class="on-logo"
                 src="${pageContext.request.contextPath}/assets/oikonaosLogo.png"
                 alt="Logo OikoNaos" />
        </a>
        <%
            Object obj = session.getAttribute("utente");
            if (obj == null) {
        %>
        <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
        |
        <a href="${pageContext.request.contextPath}/register.jsp">Registrati</a>
        <%
        } else {
        %>
        <a href="${pageContext.request.contextPath}/home.jsp">Profilo</a>
        |
        <a href="${pageContext.request.contextPath}/PrenotazioneController">Prenotazioni</a>
        |
        <a href="${pageContext.request.contextPath}/TicketController">Ticket</a>
        <%
            }
        %>
    </div>

    <div class="on-right">
        <input type="checkbox" id="onToggle" class="on-toggle" aria-hidden="true" />

        <label for="onToggle"
               class="on-hamb"
               aria-label="Apri menu di navigazione"
               role="button">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="3" y1="6"  x2="21" y2="6"/>
                <line x1="3" y1="12" x2="21" y2="12"/>
                <line x1="3" y1="18" x2="21" y2="18"/>
            </svg>
        </label>

        <nav class="on-menu" aria-label="Menu principale">
            <%
                if (obj != null) {
                    it.unisa.oikonaos.model.Utente u =
                            (it.unisa.oikonaos.model.Utente) obj;

                    if ("SUPERVISORE".equalsIgnoreCase(u.getRuolo())) {
            %>
            <a href="${pageContext.request.contextPath}/supervisore/dashboard.jsp" style="font-weight: bold; color: #ffc107;">
                Dashboard Supervisore
            </a>
            <a href="${pageContext.request.contextPath}/AdminTicketController">
                Gestione Ticket
            </a>
            <a href="${pageContext.request.contextPath}/AdminPrenotazioniController">
                Gestione Prenotazioni
            </a>
            <hr style="border-color: rgba(255,255,255,0.1); margin: 5px 0;">
            <%
                }
            %>
            <a href="${pageContext.request.contextPath}/LogoutController">
                Logout
            </a>
            <%
                }
            %>
        </nav>
    </div>
</header>