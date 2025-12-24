<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css" />

<%
    Object obj = session.getAttribute("utente");
    it.unisa.oikonaos.model.Utente u = null;
    if (obj != null) {
        u = (it.unisa.oikonaos.model.Utente) obj;
    }
%>

<header class="on-navbar">

    <div class="on-left">
        <a href="${pageContext.request.contextPath}/index.jsp"
           class="on-logo-link"
           aria-label="Vai alla home">
            <img class="on-logo"
                 src="${pageContext.request.contextPath}/assets/oikonaosLogo.png"
                 alt="Logo OikoNaos" />
        </a>

        <% if (u == null) { %>
        <a href="${pageContext.request.contextPath}/login.jsp">Login</a>
        |
        <a href="${pageContext.request.contextPath}/register.jsp">Registrati</a>

        <% } else if ("SUPERVISORE".equalsIgnoreCase(u.getRuolo())) { %>
        <a href="${pageContext.request.contextPath}/supervisore/home.jsp">Home</a>
        |
        <a href="${pageContext.request.contextPath}/SupervisorePrenotazioniController">Prenotazioni</a>
        |
        <a href="${pageContext.request.contextPath}/SupervisoreTicketController">Ticket</a>

        <% } else { %>
        <a href="${pageContext.request.contextPath}/home.jsp">Profilo</a>
        |
        <a href="${pageContext.request.contextPath}/PrenotazioneController">Prenotazioni</a>
        |
        <a href="${pageContext.request.contextPath}/TicketController">Ticket</a>
        <% } %>
    </div>

    <div class="on-right">
        <input type="checkbox" id="onToggle" class="on-toggle" aria-hidden="true" />

        <label for="onToggle" class="on-hamb" aria-label="Apri menu di navigazione" role="button">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor"
                 stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="3" y1="6"  x2="21" y2="6"/>
                <line x1="3" y1="12" x2="21" y2="12"/>
                <line x1="3" y1="18" x2="21" y2="18"/>
            </svg>
        </label>

        <nav class="on-menu" aria-label="Menu principale">
            <% if (u != null) { %>
            <% if ("SUPERVISORE".equalsIgnoreCase(u.getRuolo())) { %>
            <a href="${pageContext.request.contextPath}/supervisore/home.jsp">
                Area Supervisore
            </a>
            <% } %>

            <a href="${pageContext.request.contextPath}/LogoutController">
                Logout
            </a>
            <% } %>
        </nav>
    </div>
</header>
