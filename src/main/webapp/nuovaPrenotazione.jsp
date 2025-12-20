<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head><title>Nuova Prenotazione</title></head>
    <body>
    <jsp:include page="header.jsp" />
        <h2>Prenota la tua postazione</h2>
        <form action="PrenotazioneController" method="post">

            <label>Data:</label><br>
            <input type="date" name="data" required>
            <br><br>

            <label>Ambiente:</label><br>
            <select name="ambiente" required>
                <option value="" disabled selected>-- Seleziona ambiente --</option>
                <option value="SALA_STUDIO">Sala Studio</option>
                <option value="PALESTRA">Palestra</option>
            </select>
            <br><br>

            <label>Postazione:</label><br>
            <select name="idPostazione" required>
                <option value="" disabled selected>-- Seleziona postazione --</option>
                <option value="1">Postazione 1</option>
                <option value="2">Postazione 2</option>
                <option value="3">Postazione 3</option>
            </select>
            <br><br>

            <label>Fascia Oraria:</label><br>
            <select name="idFascia" required>
                <option value="" disabled selected>-- Seleziona fascia oraria --</option>
                <option value="1">08:00 - 12:00</option>
                <option value="2">12:00 - 15:00</option>
                <option value="3">15:00 - 18:00</option>
            </select>
            <br><br>
            <button type="submit">Conferma Prenotazione</button>
        </form>

    </body>
</html>
