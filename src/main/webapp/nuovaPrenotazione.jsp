<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Nuova Prenotazione</title></head>
<body>
<%@ include file="/include/header-navbar.jsp" %>
<h2>Prenota la tua postazione</h2>
<form action="PrenotazioneController" method="post">
    Data: <input type="date" name="data" required><br><br>
    Postazione (ID): <input type="number" name="idPostazione" required><br><br>
    Fascia Oraria (ID): <input type="number" name="idFascia" required><br><br>
    <button type="submit">Conferma Prenotazione</button>
</form>
</body>
</html>
