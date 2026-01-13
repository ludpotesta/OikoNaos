<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Iscrizione completata</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="confirmation-page">

    <div class="confirmation-card">
        <div class="confirmation-icon">✅</div>

        <h2>Iscrizione avvenuta con successo</h2>

        <p>
            Sei stato correttamente iscritto all’evento.
        </p>

        <a href="${pageContext.request.contextPath}/BachecaEventiController"
           class="btn primary">
            Torna alla bacheca eventi
        </a>
    </div>

</main>

<footer class="footer">
    © 2025 OikoNaos — Co-housing, insieme.
</footer>

</body>
</html>
