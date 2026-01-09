<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Area Coinquilino</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<%
    Utente u = (Utente) session.getAttribute("utente");
    String errore = request.getParameter("error");
    if ("ruolo".equals(errore)) {
%>
<script>
    alert("Accesso negato: non hai i permessi per visualizzare questa pagina.");
</script>
<%
    }
%>

<main class="dashboard">

    <!-- HEADER CON MASCOTTE + SALUTO -->
    <section class="dashboard-header header-with-mascot">

        <!-- MASCOTTE -->
        <div class="header-mascot">
            <img
                    src="${pageContext.request.contextPath}/assets/ecateMascotte.png"
                    alt="Mascotte Ecate"
            />
        </div>

        <!-- TESTO -->
        <div class="header-text">
            <h1 class="dashboard-title">
                Ciao, <%= u.getNome() %>
            </h1>
            <p class="dashboard-subtitle">
                Benvenuto nella tua area personale.
            </p>
        </div>

    </section>

    <!-- GRID DELLE FUNZIONALITÀ -->
    <section class="dashboard-grid">

        <!-- PRENOTAZIONI -->
        <a href="${pageContext.request.contextPath}/PrenotazioneController"
           class="dashboard-card active">
            <span class="icon">📅</span>
            <h3>Prenotazioni</h3>
            <p>Gestisci le tue prenotazioni degli spazi comuni.</p>
        </a>

        <!-- TICKET -->
        <a href="${pageContext.request.contextPath}/TicketController"
           class="dashboard-card active">
            <span class="icon">🎫</span>
            <h3>Ticket</h3>
            <p>Invia e monitora le richieste di assistenza.</p>
        </a>

        <!-- PROFILO (NON IMPLEMENTATO) -->
        <a href="${pageContext.request.contextPath}/ModificaDatiController"
           class="dashboard-card active">
            <span class="icon">👤</span>
            <h3>Profilo</h3>
            <p>Gestisci i tuoi dati personali.</p>
        </a>

        <!-- BACHECA EVENTI -->
        <div class="dashboard-card disabled">
            <span class="icon">📌</span>
            <h3>Bacheca Eventi</h3>
            <p>Avvisi e comunicazioni della community.</p>
            <span class="badge">Coming soon</span>
        </div>

        <!-- SPESE -->
        <a href="${pageContext.request.contextPath}/SpeseController"
           class="dashboard-card active">
            <span class="icon">💰</span>
            <h3>Spese</h3>
            <p>Gestione delle spese condivise.</p>
        </a>

        <!-- RISORSE -->
        <div class="dashboard-card disabled">
            <span class="icon">🧰</span>
            <h3>Risorse</h3>
            <p>Strumenti e risorse comuni.</p>
            <span class="badge">Coming soon</span>
        </div>

    </section>

</main>
<footer class="footer">
    &copy; 2025 OikoNaos - Area Coinquilino
</footer>

</body>
</html>
