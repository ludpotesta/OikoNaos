<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Reimposta password</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="login-page">

    <section class="login-card">

        <h2>Reimposta password</h2>

        <%
            String token = request.getParameter("token");
            if (token == null || token.isBlank()) {
        %>
        <p class="login-error">
            Link di reset non valido o scaduto.
        </p>
        <%
                return;
            }
        %>

        <form action="${pageContext.request.contextPath}/ResetPasswordController" method="post">

            <!-- token passato via URL -->
            <input type="hidden"
                   name="token"
                   value="<%= token %>">

            <input type="password"
                   name="nuovaPassword"
                   placeholder="Nuova password"
                   required>

            <input type="password"
                   name="confermaPassword"
                   placeholder="Conferma password"
                   required>

            <button type="submit">
                Aggiorna password
            </button>

        </form>

        <% if ("token".equals(request.getParameter("error"))) { %>
        <p class="login-error">
            Token non valido o scaduto.
        </p>
        <% } %>

    </section>

</main>

<footer class="footer">
    © 2025 OikoNaos
</footer>

</body>
</html>
