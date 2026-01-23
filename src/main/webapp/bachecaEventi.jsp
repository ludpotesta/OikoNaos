<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.dto.EventoBachecaDTO, java.util.List" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Bacheca Eventi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <header class="page-header">
        <h1 class="page-title">Bacheca Eventi</h1>
        <p class="page-subtitle">
            Eventi e attività della tua comunità
        </p>
    </header>

    <%
        List<EventoBachecaDTO> eventi =
                (List<EventoBachecaDTO>) request.getAttribute("eventi");
    %>

    <% if (eventi == null || eventi.isEmpty()) { %>

    <div class="card">
        <div class="empty-state">
            Nessun evento disponibile al momento.
        </div>
    </div>

    <% } else { %>

    <% for (EventoBachecaDTO e : eventi) { %>

    <div class="card" style="margin-bottom:24px;">

        <!-- HEADER EVENTO -->
        <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:20px;">
            <div>
                <h2 style="margin:0 0 6px;">
                    <%= e.getTitolo() %>
                </h2>

                <div style="color:var(--muted); font-size:0.95rem;">
                    📅 <%= e.getDataInizioFormatted() %> – <%= e.getDataFineFormatted() %><br>
                    📍 <%= e.getLuogo() %>
                </div>
            </div>

            <span class="status">
                Posti disponibili: <%= e.getPostiDisponibili() %>
            </span>
        </div>

        <!-- DESCRIZIONE -->
        <p style="margin:18px 0; line-height:1.6;">
            <%= e.getDescrizione() %>
        </p>

        <!-- AZIONI -->
        <div class="card-actions" style="justify-content:flex-start; gap:12px;">

            <% if (e.isIscrivibile()) { %>
            <a class="btn primary"
               href="${pageContext.request.contextPath}/IscrizioneEventoController?idEvento=<%= e.getIdEvento() %>">
                Iscriviti
            </a>
            <% } %>

            <% if (e.isDisiscrivibile()) { %>
            <a class="btn ghost"
               href="${pageContext.request.contextPath}/DisiscrizioneEventoController?idEvento=<%= e.getIdEvento() %>">
                Disiscriviti
            </a>
            <% } %>

        </div>

    </div>
    <% } %>
    <% } %>

</main>
</body>
</html>
