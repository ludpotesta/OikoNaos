<<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<!DOCTYPE html>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <title>OikoNaos - Home</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    </head>
    <body>

        <%@ include file="/include/header-navbar.jsp" %>

        <%
            obj = session.getAttribute("utente");
            if (obj == null) {
        %>
        <h2>Errore: utente non in sessione</h2>
        <%
                return;
            }

            Utente utente = (Utente) obj;
        %>

        <main class="container">
            <h1>
                Benvenuto, <%= utente.getNome() %> <%= utente.getCognome() %>
            </h1>

            <p>
                Ruolo: <strong><%= utente.getRuolo() %></strong>
            </p>

            <a href="<%= request.getContextPath() %>/LogoutController">
                Logout
            </a>
        </main>

    </body>
</html>

