<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.unisa.oikonaos.model.Utente" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />

<%
    Object obj = session.getAttribute("utente");
    Utente u = (obj instanceof Utente) ? (Utente) obj : null;
%>

<header class="topbar">

    <!-- BRAND -->
    <div class="brand">
        <a href="${pageContext.request.contextPath}/index.jsp"
           class="brand-link">
            <img class="logo"
                 src="${pageContext.request.contextPath}/assets/oikonaosLogo.png"
                 alt="Logo OikoNaos" />
            <span class="brand-name">
                Oiko<span class="brand-accent">Naos</span>
            </span>
        </a>
    </div>

    <!-- NAV LINKS -->
    <nav class="nav-links">

        <%-- ======================
             UTENTE NON LOGGATO
             ====================== --%>
        <% if (u == null) { %>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/login.jsp">
            Login
        </a>

        <a class="btn primary"
           href="${pageContext.request.contextPath}/register.jsp">
            Registrati
        </a>

        <%-- ======================
             SUPERVISORE
             ====================== --%>
        <% } else if ("SUPERVISORE".equalsIgnoreCase(u.getRuolo())) { %>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/supervisore/home.jsp">
            Home
        </a>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/SupervisorePrenotazioniController?action=list">
            Prenotazioni
        </a>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/SupervisoreTicketController?action=list">
            Ticket
        </a>

        <%-- ======================
             COINQUILINO
             ====================== --%>
        <% } else { %>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/home.jsp">
            Profilo
        </a>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/PrenotazioneController?action=list">
            Prenotazioni
        </a>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/TicketController?action=list">
            Ticket
        </a>

        <% } %>

    </nav>

    <!-- USER MENU (solo se loggato) -->
    <% if (u != null) { %>
    <div class="user-menu-container">

        <button type="button"
                class="burger"
                id="menuBtn"
                aria-label="Menu utente"
                onclick="toggleMenu()">
            <span></span>
            <span></span>
            <span></span>
        </button>

        <div class="menu" id="userMenu">
            <div class="menu-header">
                <small>Loggato come</small>
                <strong><%= u.getNome() %></strong>
            </div>

            <a href="${pageContext.request.contextPath}/LogoutController"
               class="logout-link">
                Esci
            </a>
        </div>

    </div>
    <% } %>

</header>

<script>
    function toggleMenu() {
        const menu = document.getElementById("userMenu");
        if (!menu) return;
        menu.style.display = (menu.style.display === "block") ? "none" : "block";
    }

    // chiude il menu cliccando fuori
    document.addEventListener("click", function (e) {
        const menu = document.getElementById("userMenu");
        const btn = document.getElementById("menuBtn");
        if (!menu || !btn) return;
        if (!menu.contains(e.target) && !btn.contains(e.target)) {
            menu.style.display = "none";
        }
    });
</script>
