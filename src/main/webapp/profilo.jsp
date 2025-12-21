<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<!DOCTYPE html>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <title>OikoNaos - Home</title>
        <%-- Link al CSS originale --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    </head>

    <body>
        <%-- 1. Includiamo la Navbar che abbiamo creato --%>
        <jsp:include page="header.jsp" />

        <%
            // 2. Correzione: aggiunto 'Object' davanti a obj
            Object obj = session.getAttribute("utente");
            if (obj == null) {
        %>

        <div class="container">
            <h2>Errore: sessione scaduta o utente non loggato</h2>
            <a href="login.jsp">Torna al Login</a>
        </div>

        <%
                return;
            }
            Utente utente = (Utente) obj;
        %>

        <main class="container" style="padding: 20px; font-family: sans-serif;">
            <h1>
                Benvenuto, <%= utente.getNome() %> <%= utente.getCognome() %>
            </h1>

            <p style="background: #f0f0f0; padding: 10px; display: inline-block; border-radius: 5px;">
                Il tuo ruolo attuale è: <strong><%= utente.getRuolo() %></strong>
            </p>

            <hr>
            <h3>Cosa vuoi fare oggi?</h3>
            <ul>
                <li>Gestire le tue <strong>Prenotazioni</strong></li>
                <li>Inviare un <strong>Ticket</strong> di assistenza</li>
                <% if(utente.getRuolo().equalsIgnoreCase("SUPERVISORE")) { %>
                <li style="color: red;">Accedere alle funzioni <strong>Supervisore</strong></li>
                <% } %>
            </ul>
        </main>
    </body>
</html>

