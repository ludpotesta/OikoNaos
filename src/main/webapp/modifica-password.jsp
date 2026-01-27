<%--
  Created by IntelliJ IDEA.
  User: Luigi
  Date: 26/01/26
  Time: 22:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos - Modifica Password</title>
    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/webapp/css/main.css" />
</head>
<body>
<jsp:include page="/include/header-navbar.jsp" />

<main class="login-page">
    <div class="login-wrapper">
        <section class="login-card">
            <h2>Modifica Password</h2>

            <%
                String errore = (String) request.getAttribute("errore");
                boolean recupero = "true".equals(request.getParameter("recupero"));
            %>
            <% if (errore != null) { %>
            <div class="login-error">
                <%= errore %>
            </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/modifica-password" method="post">
                <% if (recupero) { %>
                <input type="hidden" name="recupero" value="true">
                <% } %>

                <input type="password" name="vecchiaPassword" placeholder="<%= recupero ? "Password Temporanea" : "Vecchia Password" %>" required>
                <input type="password" name="nuovaPassword" placeholder="Nuova Password" required>
                <input type="password" name="confermaPassword" placeholder="Conferma Nuova Password" required>

                <button type="submit">Aggiorna Password</button>
            </form>

            <% if (!recupero) { %>
            <p style="margin-top: 16px; text-align: center;">
                <a href="${pageContext.request.contextPath}/home.jsp" style="color: #dbe5f0; font-size: 0.9rem;">
                    Torna alla Home
                </a>
            </p>
            <% } %>
        </section>
    </div>
</main>

<footer class="footer">
    <small>© 2025 OikoNaos — Co-housing, insieme.</small>
</footer>
</body>
</html>
