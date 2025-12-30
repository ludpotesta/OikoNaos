<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unisa.oikonaos.model.Prenotazione" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Le mie prenotazioni</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <!-- HEADER PAGINA -->
    <header class="page-header">
        <h1 class="title">Le mie Prenotazioni</h1>
    </header>

    <%
        List<Prenotazione> prenotazioni =
                (List<Prenotazione>) request.getAttribute("listaPrenotazioni");
    %>

    <!-- CARD -->
    <section class="card">

        <% if (prenotazioni == null || prenotazioni.isEmpty()) { %>

        <p class="empty-state">
            Non hai prenotazioni attive.
        </p>

        <% } else { %>

        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Data</th>
                    <th>Ambiente</th>
                    <th>Postazione</th>
                    <th>Fascia oraria</th>
                    <th>Azioni</th>
                </tr>
                </thead>

                <tbody>
                <% for (Prenotazione p : prenotazioni) { %>
                <tr>
                    <td><%= p.getData() %></td>
                    <td><%= p.getNomeAmbiente() %></td>
                    <td>Postazione <%= p.getNumeroPostazione() %></td>
                    <td>
                        <%= p.getOrarioInizio().toLocalTime().toString().substring(0,5) %>
                        –
                        <%= p.getOrarioFine().toLocalTime().toString().substring(0,5) %>
                    </td>
                    <td>
                        <button class="btn ghost"
                                type="button"
                                onclick="openModal(<%= p.getIdPrenotazione() %>)">
                            Annulla
                        </button>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>

        <% } %>

        <div class="card-actions">
            <a href="nuovaPrenotazione.jsp" class="btn primary">
                Nuova prenotazione
            </a>
        </div>

    </section>
</main>

<!-- MODAL (LOGICA IDENTICA) -->
<div class="modal-overlay" id="modal">
    <div class="modal">
        <h3>Conferma annullamento</h3>
        <p>Sei sicuro di voler annullare questa prenotazione?</p>

        <form action="${pageContext.request.contextPath}/PrenotazioneController"
              method="post">

            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="idPrenotazione" id="idPrenotazione">

            <button type="button" class="btn ghost" onclick="closeModal()">No</button>
            <button type="submit" class="btn primary">Sì, annulla</button>
        </form>
    </div>
</div>

<script>
    function openModal(id) {
        document.getElementById("idPrenotazione").value = id;
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
