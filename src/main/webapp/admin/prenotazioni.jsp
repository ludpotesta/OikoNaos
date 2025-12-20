<%@ page import="it.unisa.oikonaos.model.Prenotazione, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Admin - Prenotazioni</title></head>
<body>
<jsp:include page="../header.jsp" />
<h1>Riepilogo Tutte le Prenotazioni</h1>
<table border="1" cellpadding="10">
    <tr><th>ID</th><th>Data</th><th>ID Utente</th><th>Postazione</th></tr>
    <%
        List<Prenotazione> lista = (List<Prenotazione>) request.getAttribute("listaGlobalePrenotazioni");
        if (lista != null) {
            for (Prenotazione p : lista) {
    %>
    <tr>
        <td><%= p.getIdPrenotazione() %></td>
        <td><%= p.getData() %></td>
        <td><%= p.getIdUtente() %></td>
        <td><%= p.getIdPostazione() %></td>
    </tr>
    <% } } %>
</table>
</body>
</html>
