<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Ticket" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unisa.oikonaos.dto.AllegatoDTO" %>
<%@ page import="java.net.URLEncoder" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Dettaglio Ticket</title>

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

        .ticket-header h2 {
            margin: 0;
        }

        .ticket-status {
            padding: 6px 14px;
            border-radius: 999px;
            font-size: 0.85rem;
            font-weight: 700;
            background: rgba(59, 130, 246, 0.12);
            color: var(--primary);
        }

        .ticket-meta {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }

        .ticket-meta div {
            font-size: 0.95rem;
        }

        .ticket-meta strong {
            color: var(--muted);
            display: block;
            font-size: 0.75rem;
            text-transform: uppercase;
            margin-bottom: 4px;
        }

        .ticket-description {
            margin-top: 24px;
            line-height: 1.6;
            white-space: pre-wrap;
        }

        .allegato-item {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 10px;
            font-size: 0.95rem;
        }

        .allegato-icon {
            font-size: 1.1rem;
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
%>

<main class="dashboard">

    <div class="ticket-details-card">

        <div class="ticket-header">
            <h2><%= ticket.getTitolo() %></h2>
            <span class="ticket-status"><%= ticket.getStato() %></span>
        </div>

        <div class="ticket-meta">
            <div>
                <strong>Categoria</strong>
                <%= ticket.getCategoria() %>
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

        <div class="ticket-description">
            <strong>Descrizione</strong><br>
            <%= ticket.getDescrizione() %>
        </div>

        <%
            List<AllegatoDTO> allegati = (List<AllegatoDTO>) request.getAttribute("allegati");
        %>

        <% if (allegati != null && !allegati.isEmpty()) { %>
        <div style="margin-top: 32px;">
            <h3>Allegati</h3>

            <ul style="margin-top: 12px;">
                <% for (AllegatoDTO a : allegati) { %>
                <li class="allegato-item">
                    <span class="allegato-icon">📎</span>
                    <a href="<%= request.getContextPath() %>/download-allegato?file=<%= a.getNomeFile() %>"
                       target="_blank"
                       class="allegato-link">
                        <%= a.getNomeFile() %>
                    </a>
                </li>
                <% } %>
            </ul>
        </div>
        <% } %>

        <div class="card-actions">
            <a href="<%= request.getContextPath() %>/TicketController"
               class="btn primary">
                Torna ai miei ticket
            </a>
        </div>
    </div>

</main>

</body>
</html>

