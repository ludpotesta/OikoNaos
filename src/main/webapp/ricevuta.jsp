<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Ricevuta" %>

<%
    Ricevuta r = (Ricevuta) request.getAttribute("ricevuta");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Ricevuta di pagamento</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/main.css">

    <style>
        :root {
            --primary: inherit;
            --text: inherit;
            --muted: inherit;
            --shadow: none;
        }

        .receipt {
            max-width: 720px;
            margin: 0 auto;
            background: #fff;
            padding: 40px;
            border-radius: 16px;
            box-shadow: var(--shadow);
        }

        /* HEADER */
        .receipt-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 32px;
        }

        .receipt-header h2 {
            margin: 0;
        }

        .receipt-header small {
            color: var(--muted);
        }

        /* GRID DETTAGLI */
        .receipt-grid {
            display: grid;
            grid-template-columns: 1fr auto;
            row-gap: 12px;
            column-gap: 24px;
            margin-top: 20px;
        }

        .receipt-grid strong {
            color: var(--text);
        }

        .receipt-grid span {
            text-align: right;
            color: var(--muted);
        }

        /* TOTALE */
        .receipt-total {
            margin-top: 32px;
            padding-top: 24px;
            border-top: 2px solid #eee;
            text-align: right;
        }

        .receipt-total span {
            font-size: 1.6rem;
            font-weight: 700;
            color: var(--primary);
        }

        /* PRINT / PDF */
        @media print {
            body {
                background: white;
            }

            header,
            nav,
            .btn {
                display: none !important;
            }

            .receipt {
                box-shadow: none;
                border-radius: 0;
                padding: 0;
            }
        }
    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="dashboard">

    <h1 class="page-title">Ricevuta di pagamento</h1>

    <section class="receipt">

        <% if (r == null) { %>

        <p class="empty-state">Ricevuta non disponibile.</p>

        <% } else { %>

        <!-- HEADER -->
        <div class="receipt-header">
            <div>
                <h2>OikoNaos</h2>
                <small>Sistema di gestione condominiale</small>
            </div>

            <div style="text-align:right">
                <strong>Ricevuta</strong><br>
                <small><%= r.getDataEmissione() %></small>
            </div>
        </div>

        <!-- DETTAGLI -->
        <div class="receipt-grid">
            <strong>Codice transazione</strong>
            <span><%= r.getCodiceTransazione() %></span>

            <strong>ID Ricevuta</strong>
            <span><%= r.getIdRicevuta() %></span>
        </div>

        <!-- IMPORTO -->
        <div class="receipt-total">
            Importo pagato<br>
            <span>€ <%= r.getImporto() %></span>
        </div>

        <% } %>

    </section>

    <!-- AZIONI -->
    <div style="margin-top:32px; text-align:center">
        <button onclick="window.print()" class="btn primary">
            Scarica PDF
        </button>

        <a href="${pageContext.request.contextPath}/SpeseController"
           class="btn ghost">
            Torna alle spese
        </a>
    </div>

</main>

</body>
</html>
