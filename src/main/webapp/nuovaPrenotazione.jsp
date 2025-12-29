<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Nuova Prenotazione</title>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<h2>Prenota la tua postazione</h2>

<%
    String error = request.getParameter("error");
    if ("conflitto".equals(error)) {
%>
<p style="color:red;">Postazione già occupata in questa fascia oraria.</p>
<%
} else if ("data_passata".equals(error)) {
%>
<p style="color:red;">Non puoi prenotare una data passata.</p>
<%
    }
%>

<form action="${pageContext.request.contextPath}/PrenotazioneController"
      method="post">

    <input type="hidden" name="action" value="create">

    <!-- DATA -->
    <label>Data:</label><br>
    <input type="date"
           name="data"
           min="<%= java.time.LocalDate.now() %>"
           required>
    <br><br>

    <!-- AMBIENTE -->
    <label>Ambiente:</label><br>
    <select name="ambiente" required>
        <option value="">-- Seleziona ambiente --</option>
        <option value="1">Sala Studio</option>
        <option value="2">Palestra</option>
    </select>
    <br><br>

    <!-- POSTAZIONE -->
    <label>Postazione:</label><br>
    <select name="idPostazione" required>
        <option value="">-- Seleziona postazione --</option>
        <option value="1">Postazione 1</option>
        <option value="2">Postazione 2</option>
        <option value="3">Postazione 3</option>
    </select>
    <br><br>

    <!-- FASCIA ORARIA -->
    <label>Fascia oraria:</label><br>
    <select name="idFascia" required>
        <option value="">-- Seleziona fascia oraria --</option>
        <option value="1">08:00 - 12:00</option>
        <option value="2">12:00 - 15:00</option>
        <option value="3">15:00 - 18:00</option>
    </select>
    <br><br>

    <button type="submit">Conferma prenotazione</button>
</form>


<p>
    <a href="${pageContext.request.contextPath}/PrenotazioneController?action=list">
        Torna alle mie prenotazioni
    </a>
</p>
