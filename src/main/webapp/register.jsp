<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos - Registrazione</title>

    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&family=Playfair+Display:wght@700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>

<!-- Navbar -->
<jsp:include page="/include/header-navbar.jsp" />

<main class="hero">

    <%-- Eventuale messaggio di errore --%>
    <%
        String error = request.getParameter("error");
        if (error != null) {
    %>
    <p class="alert">Errore durante la registrazione. Verifica i dati inseriti.</p>
    <%
        }
    %>

    <form action="${pageContext.request.contextPath}/RegistrazioneController"
          method="post"
          class="form-section">

        <h2>Registrati a OikoNaos</h2>

        <div>
            <label for="nome">Nome:</label>
            <input id="nome" type="text" name="nome" required class="form-input">
        </div>

        <div>
            <label for="cognome">Cognome:</label>
            <input id="cognome" type="text" name="cognome" required class="form-input">
        </div>

        <div>
            <label for="email">Email:</label>
            <input id="email" type="email" name="email" required class="form-input">
        </div>

        <div>
            <label for="telefono">Numero di telefono:</label>
            <input id="telefono" type="tel" name="telefono" required class="form-input">
        </div>

        <div>
            <label for="username">Username:</label>
            <input id="username" type="text" name="username" required class="form-input">
        </div>

        <p style="font-size: 0.9em; color: #555;">
            Immetti una password e il codice ricevuto alla stipulazione del contratto.
        </p>

        <div>
            <label for="password">Password:</label>
            <input id="password" type="password" name="password" required class="form-input">
        </div>

        <div>
            <label for="codiceID">Codice identificativo:</label>
            <input id="codiceID" type="text" name="codiceID" required class="form-input">
        </div>

        <input type="submit" value="Registrati" class="form-submit">
    </form>

</main>

<footer class="footer">
    <small>© 2025 OikoNaos — Co-housing, insieme.</small>
</footer>

</body>
</html>

