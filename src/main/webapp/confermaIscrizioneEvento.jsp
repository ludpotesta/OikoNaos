<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Iscrizione completata - OikoNaos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <div class="card" style="max-width:520px; margin:80px auto; text-align:center;">

        <!-- ICONA -->
        <div style="font-size:48px; margin-bottom:16px;">
            ✅
        </div>

        <!-- TITOLO -->
        <h1 class="page-title" style="margin-bottom:12px;">
            Iscrizione completata
        </h1>

        <!-- TESTO -->
        <p style="color:var(--muted); font-size:1.05rem; margin-bottom:32px;">
            Sei stato correttamente iscritto all’evento selezionato.
        </p>

        <!-- AZIONE -->
        <a class="btn primary"
           href="${pageContext.request.contextPath}/BachecaEventiController">
            Torna alla bacheca eventi
        </a>

    </div>

</main>

</body>
</html>
