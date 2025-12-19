<%@ page import="it.unisa.oikonaos.model.Utente" %>
<%
    // Rispetto del criterio richiesto
    Utente utente = (Utente) request.getSession().getAttribute("utente");
    if (utente == null) { response.sendRedirect("login.jsp"); }
%>
<html>
<body>
<h2>Nuova Prenotazione per: <%= utente.getNome() %></h2>
<form action="PrenotazioneControl" method="post">
    Data: <input type="date" name="data" required><br>
    ID Postazione: <input type="number" name="idPostazione" required><br>
    ID Fascia Oraria: <input type="number" name="idFascia" required><br>
    <button type="submit">Conferma Prenotazione</button>
</form>
</body>
</html>
