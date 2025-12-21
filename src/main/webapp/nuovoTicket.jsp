<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <title>Nuovo Ticket</title>
    </head>

    <body>

        <jsp:include page="/include/header-navbar.jsp" />

        <h2>Apri una Segnalazione</h2>

        <form action="TicketController"
              method="post"
              enctype="multipart/form-data">

        <label>Titolo:</label><br>
            <input type="text" name="titolo" required>
            <br><br>

            <label>Descrizione:</label><br>
            <textarea name="descrizione" rows="5" cols="40" required></textarea>
            <br><br>

            <label>Categoria:</label><br>
            <select name="categoria" required>
                <option value="Hardware">Hardware</option>
                <option value="Software">Software</option>
            </select>
            <br><br>

            <label>Priorità:</label><br>
            <select name="priorita" required>
                <option value="BASSA">Bassa</option>
                <option value="MEDIA">Media</option>
                <option value="ALTA">Alta</option>
            </select>
            <br><br>

            <label>Allegati (max 5):</label><br>
            <input type="file" name="allegati" multiple accept=".pdf,.jpg,.png,.doc,.docx">
            <br><br>

            <button type="submit">Invia Segnalazione</button>

        </form>

        <p>
            <a href="TicketController">Torna ai miei ticket</a>
        </p>

    </body>
</html>
