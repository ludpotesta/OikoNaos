<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>I miei Ticket</title>

    <style>
        .modal-overlay {
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.5);
            display: none;
            justify-content: center;
            align-items: center;
        }
        .modal {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }
    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

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
        if (lista != null && !lista.isEmpty()) {
            for (Ticket t : lista) {
    %>
    <tr>
        <td><%= t.getTitolo() %></td>
        <td><%= t.getCategoria() %></td>
        <td><%= t.getPriorita() %></td>
        <td><strong><%= t.getStato() %></strong></td>
        <td>
            <% if ("APERTO".equals(t.getStato())) { %>
            <button type="button"
                    onclick="openModal(<%= t.getIdTicket() %>)">
                Cancella
            </button>
            <% } else { %>
            -
            <% } %>
        </td>
    </tr>
    <% } } %>
</table>

<br>
<a href="TicketController?action=new">Apri un nuovo ticket</a>

<!-- MODAL CONFERMA -->
<div class="modal-overlay" id="modal">
    <div class="modal">
        <h3>Conferma annullamento</h3>
        <p>Sei sicuro di voler annullare questo ticket?</p>

        <form action="TicketController" method="post">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="idTicket" id="idTicket">

            <button type="button" onclick="closeModal()">No</button>
            <button type="submit">Sì, annulla</button>
        </form>
    </div>
</div>

<script>
    function openModal(id) {
        document.getElementById("idTicket").value = id;
        document.getElementById("modal").style.display = "flex";
    }

    function closeModal() {
        document.getElementById("modal").style.display = "none";
    }
</script>

</body>
</html>
