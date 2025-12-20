<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Nuovo Ticket</title></head>
<body>
<jsp:include page="header.jsp" />
<h2>Apri una Segnalazione</h2>
<form action="TicketController" method="post">
    Titolo: <input type="text" name="titolo" required><br><br>
    Descrizione: <br><textarea name="descrizione"></textarea><br><br>
    Categoria:
    <select name="categoria">
        <option value="Hardware">Hardware</option>
        <option value="Software">Software</option>
    </select><br><br>
    Priorità:
    <select name="priorita">
        <option value="BASSA">Bassa</option>
        <option value="MEDIA">Media</option>
        <option value="ALTA">Alta</option>
    </select><br><br>
    <button type="submit">Invia Segnalazione</button>
</form>
</body>
</html>
