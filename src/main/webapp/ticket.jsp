<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>I miei Ticket</title>

    <style>
        table {
            border-collapse: collapse;
            width: 80%;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #333;
            padding: 8px;
            text-align: center;
        }
        th {
            background-color: #f0f0f0;
        }

        /* MODAL */
        .modal-overlay {
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.5);
            display: none;          /* CHIUSO DI DEFAULT */
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        .modal {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
            width: 320px;
        }
        .modal button {
            margin: 5px;
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

<table>
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
            <% if ("APERTO".equalsIgnoreCase(t.getStato())) { %>
            <!-- type="button" OBBLIGATORIO -->
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

        <form action="${pageContext.request.contextPath}/TicketController"
              method="post">

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

    // RETE DI SICUREZZA: chiude sempre il modal al caricamento pagina
    document.addEventListener("DOMContentLoaded", function () {
        const modal = document.getElementById("modal");
        if (modal) {
            modal.style.display = "none";
        }
    });
</script>

</body>
</html>
