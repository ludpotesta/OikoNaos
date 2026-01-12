<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Recupero password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="login-page">

    <section class="login-card">

        <h2>Recupero password</h2>

        <form action="RichiestaResetPasswordController" method="post">

            <input type="email" name="email" placeholder="Inserisci la tua email" required>
            <button type="submit">
                Richiedi reset
            </button>

        </form>

        <%
            String success = request.getParameter("success");
            String link = request.getParameter("link");

            if ("ok".equals(success)) {
        %>
        <p class="info-text">
            Se l’email esiste, riceverai le istruzioni per il reset.
        </p>

        <% if (link != null) { %>
        <p>
            <strong>Link reset (dev):</strong><br>
            <a href="<%= link %>"><%= link %></a>
        </p>
        <% } %>

        <%
            }
        %>

    </section>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos
</footer>

</body>
</html>
