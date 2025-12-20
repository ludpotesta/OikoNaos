<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>
<html>
<head><title>I miei Ticket</title></head>
<body>
<h1>I tuoi Ticket di Assistenza</h1>
<table border="1">
    <tr>
        <th>Titolo</th><th>Categoria</th><th>Priorità</th>
    </tr>
    <%
        List<Ticket> lista = (List<Ticket>) request.getAttribute("listaTicket");
        if(lista != null) {
            for(Ticket t : lista) {
    %>
    <tr>
        <td><%= t.getTitolo() %></td>
        <td><%= t.getCategoria() %></td>
        <td><%= t.getPriorita() %></td>
    </tr>
    <%      }
    }
    %>
</table>
<br>
<a href="nuovoTicket.jsp">Apri un nuovo ticket</a>
</body>
</html>
