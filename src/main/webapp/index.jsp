<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos</title>

    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&family=Playfair+Display:wght@700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>

<!-- NAVBAR -->
<jsp:include page="/include/header-navbar.jsp" />

<main>

    <!-- BLOCCO BLU IN CIMA -->
    <section class="landing-top">
        <div class="landing-top-card">
            <h1>
                Abitare insieme,<br>
                costruire comunità
            </h1>
            <p>
                OikoNaos è la piattaforma italiana dedicata al
                co-housing sostenibile e alla gestione degli
                spazi condivisi.
            </p>
        </div>
    </section>

    <!-- SEZIONE INFERIORE: LOGO + TESTO -->
    <section class="landing-bottom">

        <!-- LOGO TEMPIO -->
        <div class="about-logo">
            <img
                    src="${pageContext.request.contextPath}/assets/oikonaosLogo.png"
                    alt="Tempio OikoNaos"
            />
        </div>

        <!-- TESTO "COS'È OIKONAOS" -->
        <div class="landing-info">
            <h2>
                <span class="word">OikoNaos</span>
                <span class="greek">(οἶκοναός)</span>
            </h2>

            <p>
                Il nome <strong>OikoNaos</strong> deriva dal greco:
                <em>oîkos</em> (casa) e <em>naós</em> (tempio).
                Il tempio rappresenta uno spazio condiviso,
                simbolo di incontro, cooperazione e identità comune,
                come descritto nel Requirements Analysis Document.
            </p>

            <p>
                OikoNaos nasce per facilitare la vita nelle
                co-community, offrendo strumenti per la
                prenotazione degli spazi, la gestione delle
                segnalazioni e la collaborazione quotidiana.
            </p>
        </div>

    </section>
</main>

<footer class="footer">
    <small>© 2025 OikoNaos — Co-housing, insieme.</small>
</footer>

</body>
</html>
