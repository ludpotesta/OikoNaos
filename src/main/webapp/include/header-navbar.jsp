<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="it.unisa.oikonaos.model.Utente" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />

<%
    Object obj = session.getAttribute("utente");
    Utente u = (obj instanceof Utente) ? (Utente) obj : null;
%>

<header class="topbar">

    <!-- LOGO / BRAND -->
    <div class="brand">
        <a href="<%=
            (u == null)
                ? request.getContextPath() + "/index.jsp"
                : (
                    "SUPERVISORE".equalsIgnoreCase(u.getRuolo())
                        ? request.getContextPath() + "/supervisore/home.jsp"
                        : request.getContextPath() + "/home.jsp"
                  )
        %>" class="brand-link">

            <img class="logo"
                 src="${pageContext.request.contextPath}/assets/oikonaosLogo.png"
                 alt="Logo OikoNaos" />

            <span class="brand-name">
                Oiko<span class="brand-accent">Naos</span>
            </span>
        </a>
    </div>

    <div></div>

    <!-- NAV -->
    <nav class="nav-links">

        <% if (u == null) { %>

        <a class="btn ghost"
           href="${pageContext.request.contextPath}/login.jsp">
            Login
        </a>

        <a class="btn primary"
           href="${pageContext.request.contextPath}/register.jsp">
            Registrati
        </a>

        <% } else { %>

        <!-- MENU UTENTE -->
        <div class="user-menu-container">

            <button type="button"
                    class="burger"
                    id="menuBtn"
                    onclick="toggleMenu()"
                    aria-label="Menu utente">
                <span></span>
                <span></span>
                <span></span>
            </button>

            <div class="menu" id="userMenu">
                <div class="menu-header">
                    <small>Loggato come</small>
                    <strong><%= u.getNome() %></strong>
                </div>

                <!-- MAPPA -->
                <a href="${pageContext.request.contextPath}/MappaController"
                   class="menu-link">
                    🗺️ Mappa struttura
                </a>

                <hr>

                <!-- LOGOUT -->
                <a href="${pageContext.request.contextPath}/LogoutController"
                   class="logout-link">
                    Esci
                </a>
            </div>

        </div>

        <% } %>

    </nav>

</header>

<script>
    function toggleMenu() {
        const menu = document.getElementById("userMenu");
        if (!menu) return;
        menu.style.display = (menu.style.display === "block") ? "none" : "block";
    }

    document.addEventListener("click", function (e) {
        const menu = document.getElementById("userMenu");
        const btn = document.getElementById("menuBtn");
        if (!menu || !btn) return;
        if (!menu.contains(e.target) && !btn.contains(e.target)) {
            menu.style.display = "none";
        }
    });
</script>
