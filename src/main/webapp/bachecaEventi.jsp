<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unisa.oikonaos.dto.EventoBachecaDTO" %>

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

    <%
        String success = request.getParameter("success");
        String error = request.getParameter("error");
    %>

    <h1 class="page-title">Bacheca Eventi</h1>
    <p class="page-subtitle">
        Eventi e attività della tua comunità
    </p>

    <!-- MODALE SUCCESSO -->
    <% if ("iscritto".equals(success)) { %>
    <div class="modal-overlay">
        <div class="modal-box success">
            <div class="modal-icon">✅</div>
            <h3>Iscrizione avvenuta con successo</h3>
            <p>Sei stato correttamente iscritto all’evento.</p>
            <a href="<%= request.getContextPath() %>/BachecaEventiController"
               class="btn primary">Esci</a>
        </div>
    </div>
    <% } %>

    <!-- MODALE ERRORE -->
    <% if ("posti".equals(error)) { %>
    <div class="modal-overlay">
        <div class="modal-box danger">
            <div class="modal-icon">❌</div>
            <h3>Posti esauriti</h3>
            <p>Non è più possibile iscriversi a questo evento.</p>
            <a href="<%= request.getContextPath() %>/BachecaEventiController"
               class="btn secondary">Torna alla bacheca</a>
        </div>
    </div>
    <% } %>

    <section class="event-board-grid">

        <%
            List<EventoBachecaDTO> eventi =
                    (List<EventoBachecaDTO>) request.getAttribute("eventi");

            if (eventi == null || eventi.isEmpty()) {
        %>
        <p class="empty-state">Nessun evento disponibile al momento.</p>
        <%
        } else {
            for (EventoBachecaDTO e : eventi) {

                // Icona in base al titolo
                String titolo = e.getTitolo().toLowerCase();
                String icon = "📌";

                if (titolo.contains("cucina")) icon = "🍳";
                else if (titolo.contains("pilates")) icon = "🏋️";
                else if (titolo.contains("picnic")) icon = "🧺";
                else if (titolo.contains("assemblea")) icon = "🗣️";
        %>

        <div class="event-card-white">

            <!-- ICONA -->
            <div class="event-icon">
                <span><%= icon %></span>
            </div>

            <!-- CONTENUTO -->
            <div class="event-content">

                <h3 class="event-title"><%= e.getTitolo() %></h3>

                <p class="event-meta">
                    📅 <%= e.getDataInizio().toLocalDate() %>
                    – <%= e.getDataInizio().toLocalTime() %><br>
                    📍 <%= e.getLuogo() %>
                </p>

                <p class="event-description">
                    <%= e.getDescrizione() %>
                </p>

                <p class="event-slots">
                    Posti disponibili:
                    <strong><%= e.getPostiDisponibili() %></strong>
                </p>

                <div class="event-footer">

                    <% if (e.isIscritto()) { %>
                    <span class="badge success">Iscritto</span>

                    <% } else if (e.getPostiDisponibili() <= 0) { %>
                    <span class="badge danger">Posti esauriti</span>

                    <% } else { %>
                    <form action="IscrizioneEventoController" method="post">
                        <input type="hidden" name="idEvento"
                               value="<%= e.getIdEvento() %>">
                        <button type="submit" class="btn primary">
                            Iscriviti
                        </button>
                    </form>
                    <% } %>

                </div>
            </div>

        </div>

        <%
                }
            }
        %>

    </section>
</main>

<footer class="footer">
    © 2025 OikoNaos — Co-housing, insieme.
</footer>

</body>
</html>
