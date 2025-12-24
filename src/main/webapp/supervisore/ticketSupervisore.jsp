<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>

<html>
<head>
    <title>Gestione Ticket - Supervisore</title>
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<h1>Gestione Globale Ticket</h1>

<table border="1" cellpadding="10">
    <tr>
        <th>ID</th>
        <th>Titolo</th>
        <th>Stato</th>
        <th>Azione</th>
    </tr>

    <%
        List<Ticket> lista = (List<Ticket>) request.getAttribute("listaGlobaleTicket");
        if (lista != null) {
            for (Ticket t : lista) {
    %>
    <tr>
        <td><%= t.getIdTicket() %></td>
        <td><%= t.getTitolo() %></td>
        <td><strong><%= t.getStato() %></strong></td>
        <td>
            <form method="post" action="<%= request.getContextPath() %>/SupervisoreTicketController">
                <!-- PARAMETRO ACTION (OBBLIGATORIO) -->
                <input type="hidden" name="action" value="updateStato">

                <!-- ID CORRETTO -->
                <input type="hidden" name="idTicket" value="<%= t.getIdTicket() %>">

                <!-- NOME PARAMETRO CORRETTO -->
                <select name="nuovoStato">
                    <option value="IN_LAVORAZIONE">In lavorazione</option>
                    <option value="CHIUSO">Chiuso</option>
                </select>

                <button type="submit">Aggiorna</button>
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>

</table>

</body>
</html>
