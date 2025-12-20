<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head><title>I miei Ticket</title></head>
    <body>
    <jsp:include page="header.jsp" />
        <h1>I tuoi Ticket di Assistenza</h1>
            <% if ("not_deletable".equals(request.getParameter("error"))) { %>
            <p style="color:red;">
                Impossibile annullare il ticket: è già in lavorazione.
            </p>
            <% } %>

            <% if ("deleted".equals(request.getParameter("msg"))) { %>
            <p style="color:green;">
                Ticket cancellato con successo.
            </p>
            <% } %>

        <table border="1" cellpadding="10">
            <tr>
                <th>Titolo</th>
                <th>Categoria</th>
                <th>Priorità</th>
                <th>Stato</th>
                <th>Azione</th>
            </tr>

            <%
                List<Ticket> lista = (List<Ticket>) request.getAttribute("listaTicket");
                if (lista != null) {
                    for (Ticket t : lista) {
            %>
            <tr>
                <td><%= t.getTitolo() %></td>
                <td><%= t.getCategoria() %></td>
                <td><%= t.getPriorita() %></td>
                <td><strong><%= t.getStato() %></strong></td>

                <td>
                    <% if ("APERTO".equals(t.getStato())) { %>
                    <form action="TicketController" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="idTicket" value="<%= t.getIdTicket() %>">
                        <button type="submit">Cancella</button>
                    </form>
                    <% } else { %>
                    -
                    <% } %>
                </td>
            </tr>
            <% } } %>
        </table>
    <br><a href="nuovoTicket.jsp">Apri un nuovo ticket</a>
    </body>
</html>
