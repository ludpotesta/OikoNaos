<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard Coordinamento - OikoNaos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        .card { transition: transform 0.2s; }
        .card:hover { transform: translateY(-5px); box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
    </style>
</head>
<body>

<%-- Header incluso (assicurarsi che il percorso relativo sia corretto) --%>
<jsp:include page="../include/header-navbar.jsp" />

<div class="container mt-5 mb-5">
    <div class="row mb-4">
        <div class="col-12 text-center">
            <h2 class="display-6 fw-bold text-primary">Pannello di Coordinamento</h2>
            <p class="text-muted">Azienda Coordinatrice OikoNaos</p>
        </div>
    </div>

    <%--
       Struttura basata sul Path Navigazionale UI Azienda Coordinatrice.
       Include le sezioni: Gestione Ticket, Prenotazioni, Eventi, Risorse, Spese.
    --%>
    <div class="row g-4">

        <div class="col-md-4">
            <div class="card h-100 border-warning shadow-sm">
                <div class="card-body text-center">
                    <div class="fs-1 text-warning mb-3"><i class="bi bi-tools"></i></div>
                    <h5 class="card-title">Gestione Ticket</h5>
                    <p class="card-text small">Visualizza le richieste di manutenzione, monitora le priorità e aggiorna lo stato di avanzamento.</p>
                    <a href="${pageContext.request.contextPath}/AdminTicketController" class="btn btn-outline-warning w-100">Gestisci Ticket</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card h-100 border-primary shadow-sm">
                <div class="card-body text-center">
                    <div class="fs-1 text-primary mb-3"><i class="bi bi-calendar-check"></i></div>
                    <h5 class="card-title">Gestione Prenotazioni</h5>
                    <p class="card-text small">Monitora l'occupazione delle sale studio e degli spazi comuni. Gestisci conflitti o cancellazioni.</p>
                    <a href="${pageContext.request.contextPath}/AdminPrenotazioniController" class="btn btn-outline-primary w-100">Gestisci Spazi</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card h-100 border-success shadow-sm">
                <div class="card-body text-center">
                    <div class="fs-1 text-success mb-3"><i class="bi bi-megaphone"></i></div>
                    <h5 class="card-title">Gestione Eventi</h5>
                    <p class="card-text small">Pubblica nuovi eventi in bacheca, modifica i dettagli o cancella attività programmate.</p>
                    <a href="${pageContext.request.contextPath}/AdminEventiController" class="btn btn-outline-success w-100">Gestisci Eventi</a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card h-100 border-info shadow-sm">
                <div class="card-body text-center">
                    <div class="fs-1 text-info mb-3"><i class="bi bi-box-seam"></i></div>
                    <h5 class="card-title">Risorse Condivise</h5>
                    <p class="card-text small">Gestisci l'inventario delle risorse (es. auto, attrezzi) e approva le richieste speciali.</p>
                    <a href="${pageContext.request.contextPath}/AdminRisorseController" class="
