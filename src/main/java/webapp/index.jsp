<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos</title>

    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&family=Playfair+Display:wght@700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>

<main class="hero">
    <section class="hero-copy">
        <h1 class="title">
            <span class="word">OIKONAOS</span>
            <span class="greek">(οἶκοναός)</span>
        </h1>
        <p class="tagline">Frase ad effetto che racconta la tua co-community 🌿</p>
    </section>

    <aside class="mascot">
        <div class="bubble">Ciao, sono Elate!</div>
        <img src="${pageContext.request.contextPath}/assets/ecateMascotte.png" alt="Mascotte Ecate" />
    </aside>
</main>

<footer class="footer">
    <small>© 2025 OikoNaos — Co-housing, insieme.</small>
</footer>

<script src="${pageContext.request.contextPath}/js/menu.js"></script>
</body>
</html>