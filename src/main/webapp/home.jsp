<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>

<%
    Utente u = (Utente) session.getAttribute("utente");
    if (u == null || !"SUPERVISORE".equalsIgnoreCase(u.getRuolo())) {
        response.sendRedirect(request.getContextPath() + "/home.jsp?error=ruolo");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Area Supervisore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="container">
    <h1>Area Supervisore</h1>
    <p>Gestione della comunità OikoNaos.</p>

    <div class="grid-cards">
        <a class="card" href="${pageContext.request.contextPath}/SupervisoreTicketController">
            <h3>Ticket della comunità</h3>
            <p>Visualizza e aggiorna i ticket.</p>
        </a>

        <a class="card" href="${pageContext.request.contextPath}/SupervisorePrenotazioniController">
            <h3>Prenotazioni della comunità</h3>
            <p>Gestisci le prenotazioni globali.</p>
        </a>

        <a class="card danger" href="${pageContext.request.contextPath}/LogoutController">
            <h3>Logout</h3>
            <p>Esci dal tuo account.</p>
        </a>
    </div>
</main>

</body>
</html>
