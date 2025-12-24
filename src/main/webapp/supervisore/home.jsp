<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Area Supervisore - OikoNaos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="hero">
    <h1>Area Supervisore</h1>

    <p>Benvenuto nell’area supervisore.</p>

    <ul>
        <li><a href="${pageContext.request.contextPath}/SupervisoreTicketController">
            Gestione Ticket
        </a></li>
        <li><a href="${pageContext.request.contextPath}/SupervisorePrenotazioniController">
            Gestione Prenotazioni
        </a></li>
    </ul>
</main>

</body>
</html>
