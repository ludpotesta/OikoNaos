<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unisa.oikonaos.model.Prenotazione" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Le mie prenotazioni</title>

    <style>
        table {
            border-collapse: collapse;
            width: 70%;
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
            display: none;              /* CHIUSO DI DEFAULT */
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        .modal {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            width: 300px;
            text-align: center;
        }
        .modal button {
            margin: 5px;
        }
    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<h2>Le mie prenotazioni</h2>

<%
    List<Prenotazione> prenotazioni =
            (List<Prenotazione>) request.getAttribute("listaPrenotazioni");

    if (prenotazioni == null || prenotazioni.isEmpty()) {
%>
<p>Non hai prenotazioni attive.</p>
<%
} else {
%>

<table>
    <tr>
        <th>Data</th>
        <th>Ambiente</th>
        <th>Postazione</th>
        <th>Fascia oraria</th>
        <th>Azioni</th>
    </tr>

    <% for (Prenotazione p : prenotazioni) { %>
    <tr>
        <td><%= p.getData() %></td>
        <td><%= p.getNomeAmbiente() %></td>
        <td>Postazione <%= p.getNumeroPostazione() %></td>
        <td>
            <%= p.getOrarioInizio().toLocalTime().toString().substring(0,5) %>
            -
            <%= p.getOrarioFine().toLocalTime().toString().substring(0,5) %>
        </td>
        <td>
            <!-- IMPORTANTISSIMO: type="button" -->
            <button type="button"
                    onclick="openModal(<%= p.getIdPrenotazione() %>)">
                Annulla
            </button>
        </td>
    </tr>
    <% } %>
</table>

<% } %>

<p>
    <a href="nuovaPrenotazione.jsp">Effettua una nuova prenotazione</a>
</p>

<!-- MODAL CONFERMA -->
<div class="modal-overlay" id="modal">
    <div class="modal">
        <h3>Conferma annullamento</h3>
        <p>Sei sicuro di voler annullare questa prenotazione?</p>

        <form action="${pageContext.request.contextPath}/PrenotazioneController"
              method="post">

            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="idPrenotazione" id="idPrenotazione">

            <button type="button" onclick="closeModal()">No</button>
            <button type="submit">Sì, annulla</button>
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

    // RETE DI SICUREZZA: chiude sempre il modal al load
    document.addEventListener("DOMContentLoaded", function () {
        const modal = document.getElementById("modal");
        if (modal) {
            modal.style.display = "none";
        }
    });
</script>

</body>
</html>
