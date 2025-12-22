<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Dashboard Coordinamento - OikoNaos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%-- Nota il percorso: usiamo ../ perché siamo dentro una sottocartella --%>
<jsp:include page="../include/header-navbar.jsp" />

<div class="container mt-5">
    <h2 class="mb-4">Pannello di Coordinamento Supervisore</h2>
    <div class="row g-4">
        <div class="col-md-6">
            <div class="card h-100 border-warning">
                <div class="card-body"><h5 class="card-title">Gestione Richieste (Ticket)</h5>
                    <p class="card-text">Visualizza i ticket aperti dagli utenti e aggiorna il loro stato.</p>
                    <a href="${pageContext.request.contextPath}/AdminTicketController" class="btn btn-warning">Apri Gestione</a>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="card h-100 border-primary">
                <div class="card-body">
                    <h5 class="card-title">Gestione Spazi (Prenotazioni)</h5>
                    <p class="card-text">Monitora l'occupazione delle postazioni e degli ambienti.</p>
                    <a href="${pageContext.request.contextPath}/AdminPrenotazioniController" class="btn btn-primary">Apri Gestione</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
