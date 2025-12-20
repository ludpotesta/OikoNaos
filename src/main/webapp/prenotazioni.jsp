<%@ page import="it.unisa.oikonaos.model.*, java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Le mie Prenotazioni</title></head>
<body>
<%@ include file="/include/header-navbar.jsp" %>
<h1>Storico Prenotazioni</h1>
<table border="1" cellpadding="10">
    <tr><th>Data</th><th>Postazione</th><th>Fascia</th><th>Azione</th></tr>
    <%
        List<Prenotazione> lista = (List<Prenotazione>) request.getAttribute("listaPrenotazioni");
        if(lista != null) {
            for(Prenotazione p : lista) {
    %>
    <tr>
        <td><%= p.getData() %></td>
        <td><%= p.getIdPostazione() %></td>
        <td><%= p.getIdFasciaOraria() %></td>
        <td><a href="PrenotazioneController?action=delete&id=<%= p.getIdPrenotazione() %>">Annulla</a></td>
    </tr>
    <% } } %>
</table>
<br><a href="nuovaPrenotazione.jsp">Effettua una nuova prenotazione</a>
</body>
</html>
