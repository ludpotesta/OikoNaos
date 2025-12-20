<%@ page import="it.unisa.oikonaos.model.*, java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Admin - Gestione Ticket</title></head>
<body>
<jsp:include page="../header.jsp" />
<h1>Gestione Globale Ticket</h1>
<table border="1" cellpadding="10">
    <tr><th>ID</th><th>Titolo</th><th>Stato</th><th>Azione</th></tr>
    <%
        List<Ticket> lista = (List<Ticket>) request.getAttribute("listaGlobaleTicket");
        if(lista != null) {
            for(Ticket t : lista) {
    %>
    <tr>
        <td><%= t.getIdTicket() %></td>
        <td><%= t.getTitolo() %></td>
        <td><strong><%= t.getStato() %></strong></td>
        <td>
            <form action="../AdminTicketController" method="post" style="display:inline;">
                <input type="hidden" name="action" value="updateStato">
                <input type="hidden" name="idTicket" value="<%= t.getIdTicket() %>">
                <select name="nuovoStato">
                    <option value="IN_LAVORAZIONE">Lavorazione</option>
                    <option value="CHIUSO">Chiuso</option>
                </select>
                <button type="submit">OK</button>
            </form>
        </td>
    </tr>
    <% } } %>
</table>
</body>
</html>
