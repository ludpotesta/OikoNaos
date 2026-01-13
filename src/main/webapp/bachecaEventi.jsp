<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.dto.EventoBachecaDTO, java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Bacheca Eventi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="dashboard">

    <h1 class="page-title">Bacheca Eventi</h1>

    <div class="table-container">

        <%
            List<EventoBachecaDTO> eventi =
                    (List<EventoBachecaDTO>) request.getAttribute("eventi");

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            if (eventi == null || eventi.isEmpty()) {
        %>
        <p class="muted">Non sono presenti eventi al momento.</p>
        <%
        } else {
            for (EventoBachecaDTO e : eventi) {

                // DEFINITA UNA SOLA VOLTA
                String titolo = e.getTitolo().toLowerCase();
        %>

        <!-- CARD EVENTO -->
        <div class="event-highlight">

            <!-- CONTENUTO -->
            <div class="event-highlight-body">
                <div class="bacheca-eventi-item">
                <h2 class="event-highlight-title">
                    <%= e.getTitolo() %>
                </h2>

                <div class="event-highlight-meta">
                    <span>
                        📅 <%= e.getDataInizio().format(formatter) %>
                        –
                        <%= e.getDataFine().format(formatter) %>
                    </span><br>
                    <span>📍 <%= e.getLuogo() %></span>
                </div>

                <p class="event-highlight-description">
                    <%= e.getDescrizione() %>
                </p>

                <div class="event-highlight-footer">
                    <strong>Posti disponibili:</strong>
                    <%= e.getPostiDisponibili() %>
                </div>
                </div>
                <!-- AZIONI -->
                <div class="event-highlight-actions">
                    <%
                        if (e.isIscritto()) {
                    %>
                    <span class="badge">Iscritto</span>
                    <%
                    } else if (e.getPostiDisponibili() > 0) {
                    %>
                    <form action="<%= request.getContextPath() %>/ConfermaIscrizioneEventoController"
                          method="post">
                        <input type="hidden" name="idEvento"
                               value="<%= e.getIdEvento() %>">
                        <button type="submit" class="btn primary">
                            Iscriviti
                        </button>
                    </form>
                    <%
                    } else {
                    %>
                    <span class="badge">Completo</span>
                    <%
                        }
                    %>
                </div>

            </div>

            <!-- ICONA -->
            <div class="event-highlight-icon">
                <%
                    if (titolo.contains("cucina")) {
                %> 🍳
                <%
                } else if (titolo.contains("pilates") || titolo.contains("fitness")) {
                %> 🧘
                <%
                } else if (titolo.contains("picnic")) {
                %> 🧺
                <%
                } else {
                %> 📌
                <%
                    }
                %>
            </div>

        </div>

        <%
                }
            }
        %>

    </div>

</main>

</body>
</html>
