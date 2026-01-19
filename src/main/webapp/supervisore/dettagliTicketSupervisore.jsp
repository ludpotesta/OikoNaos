<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Ticket" %>
<%@ page import="it.unisa.oikonaos.dto.AllegatoDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dettaglio Ticket - Supervisore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        .ticket-details-card {
            background: var(--card);
            border-radius: 18px;
            box-shadow: var(--shadow);
            padding: 32px;
            max-width: 900px;
            margin: 40px auto;
        }

        .ticket-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 24px;
        }

        .ticket-status {
            padding: 6px 14px;
            border-radius: 999px;
            font-size: 0.85rem;
            font-weight: 700;
            background: rgba(59,130,246,0.12);
            color: var(--primary);
        }

        .ticket-meta {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }

        .ticket-meta strong {
            color: var(--muted);
            font-size: 0.75rem;
            text-transform: uppercase;
            display: block;
            margin-bottom: 4px;
        }

        .allegato-item {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 10px;
        }

        .allegato-link {
            color: var(--primary);
            font-weight: 600;
            text-decoration: underline;
        }
    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp"/>

<%
    Ticket ticket = (Ticket) request.getAttribute("ticket");
    List<AllegatoDTO> allegati =
            (List<AllegatoDTO>) request.getAttribute("allegati");
%>

<main class="dashboard">

    <div class="ticket-details-card">

        <!-- HEADER -->
        <div class="ticket-header">
            <h2><%= ticket.getTitolo() %></h2>
            <span class="ticket-status">
                <%= ticket.getStato().replace("_", " ") %>
            </span>
        </div>

        <!-- METADATA -->
        <div class="ticket-meta">
            <div>
                <strong>Autore</strong>
                <%= ticket.getNomeAutore() %> <%= ticket.getCognomeAutore() %>
            </div>

            <div>
                <strong>Categoria</strong>
                <%= ticket.getCategoria().replace("_", " ") %>
            </div>

            <div>
                <strong>Priorità</strong>
                <%= ticket.getPriorita() %>
            </div>

            <div>
                <strong>Data apertura</strong>
                <%= ticket.getDataApertura() %>
            </div>
        </div>

        <!-- DESCRIZIONE -->
        <div class="ticket-description">
            <strong>Descrizione</strong><br>
            <%= ticket.getDescrizione() %>
        </div>

        <!-- ALLEGATI -->
        <% if (allegati != null && !allegati.isEmpty()) { %>
        <div style="margin-top:32px;">
            <h3>Allegati</h3>
            <ul>
                <% for (AllegatoDTO a : allegati) { %>
                <li class="allegato-item">
                    📎
                    <a class="allegato-link"
                       target="_blank"
                       href="<%= request.getContextPath() %>/download-allegato?file=<%= URLEncoder.encode(a.getNomeFile(), "UTF-8") %>">
                        <%= a.getNomeFile() %>
                    </a>
                </li>
                <% } %>
            </ul>
        </div>
        <% } %>

        <!-- AZIONI -->
        <div class="card-actions">
            <a href="<%= request.getContextPath() %>/SupervisoreTicketController"
               class="btn primary">
                Torna alla gestione ticket
            </a>
        </div>

    </div>

</main>

</body>
</html>