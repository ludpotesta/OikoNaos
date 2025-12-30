<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>I miei Ticket</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <!-- HEADER PAGINA -->
    <header class="page-header">
        <h1 class="page-title">I miei Ticket</h1>
        <p class="page-subtitle">
            Gestisci le richieste di assistenza inviate.
        </p>
    </header>

    <!-- CARD -->
    <section class="card">

        <% if ("not_deletable".equals(request.getParameter("error"))) { %>
        <div class="alert alert--danger">
            Impossibile annullare il ticket: è già in lavorazione.
        </div>
        <% } %>

        <% if ("deleted".equals(request.getParameter("msg"))) { %>
        <div class="alert alert--success">
            Ticket cancellato con successo.
        </div>
        <% } %>

        <%
            List<Ticket> lista = (List<Ticket>) request.getAttribute("listaTicket");
            if (lista == null || lista.isEmpty()) {
        %>

        <p class="empty-state">
            Non hai ticket aperti.
        </p>

        <% } else { %>

        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Titolo</th>
                    <th>Categoria</th>
                    <th>Priorità</th>
                    <th>Stato</th>
                    <th>Azione</th>
                </tr>
                </thead>

                <tbody>
                <% for (Ticket t : lista) { %>
                <tr>
                    <td><%= t.getTitolo() %></td>
                    <td><%= t.getCategoria() %></td>
                    <td><%= t.getPriorita() %></td>
                    <td>
                            <span class="status">
                                <%= t.getStato() %>
                            </span>
                    </td>
                    <td>
                        <% if ("APERTO".equalsIgnoreCase(t.getStato())) { %>
                        <button class="btn ghost"
                                type="button"
                                onclick="openModal(<%= t.getIdTicket() %>)">
                            Cancella
                        </button>
                        <% } else { %>
                        –
                        <% } %>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <% } %>

        <div class="card-actions">
            <a href="TicketController?action=new" class="btn primary">
                Apri nuovo ticket
            </a>
        </div>

    </section>
</main>

<!-- MODAL (LOGICA IDENTICA) -->
<div class="modal-overlay" id="modal">
    <div class="modal">
        <h3>Conferma annullamento</h3>
        <p>Sei sicuro di voler annullare questo ticket?</p>

        <form action="${pageContext.request.contextPath}/TicketController"
              method="post">

            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="idTicket" id="idTicket">

            <button type="button" class="btn ghost" onclick="closeModal()">No</button>
            <button type="submit" class="btn primary">Sì, annulla</button>
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

    document.addEventListener("DOMContentLoaded", function () {
        const modal = document.getElementById("modal");
        if (modal) modal.style.display = "none";
    });
</script>

</body>
</html>
