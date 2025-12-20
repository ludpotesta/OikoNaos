<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Apri Ticket</title></head>
<body>
<h2>Apri un nuovo Ticket di Assistenza</h2>
<form action="TicketController" method="post">
    Titolo: <input type="text" name="titolo" required><br>
    Descrizione: <textarea name="descrizione"></textarea><br>
    Categoria:
    <select name="categoria">
        <option value="Hardware">Hardware</option>
        <option value="Software">Software</option>
        <option value="Rete">Rete</option>
    </select><br>
    Priorità:
    <select name="priorita">
        <option value="BASSA">Bassa</option>
        <option value="MEDIA">Media</option>
        <option value="ALTA">Alta</option>
    </select><br>
    <button type="submit">Invia Segnalazione</button>
</form>
</body>
</html>
