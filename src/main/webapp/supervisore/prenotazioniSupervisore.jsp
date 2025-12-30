<%@ page import="it.unisa.oikonaos.model.Prenotazione, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Prenotazioni - OikoNaos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        /* Tabella coerente con il design system */
        .table-container {
            background: var(--card);
            border-radius: 18px;
            box-shadow: var(--shadow);
            padding: 24px;
            margin-top: 30px;
            overflow-x: auto;
            border: 1px solid rgba(0,0,0,0.05);
        }

        .aesthetic-table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        .aesthetic-table th {
            padding: 16px;
            color: var(--muted);
            font-weight: 700;
            text-transform: uppercase;
            font-size: 12px;
            letter-spacing: 1px;
            border-bottom: 2px solid var(--bg);
        }

        .aesthetic-table td {
            padding: 18px 16px;
            border-bottom: 1px solid var(--bg);
            color: var(--ink);
            font-size: 15px;
        }

        .aesthetic-table tr:hover {
            background-color: #f2fbfb;
        }

        .id-badge {
            color: var(--brand);
            font-weight: 800;
        }

        .status-badge {
            padding: 6px 12px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 800;
            display: inline-block;
        }

        .status-open {
            background: #fff4e5;
            color: #b76e00;
        }

        .status-closed {
            background: #f1f5f9;
            color: #475569;
        }

        .ambiente-text {
            font-family: 'Bryndan Write', cursive;
            font-size: 1.1rem;
            color: var(--ink);
        }
    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <!-- HEADER PAGINA (stesso stile coinquilino) -->
    <div class="page-header">
        <h1 class="title">Gestione Prenotazioni</h1>
        <p class="page-subtitle">Riepilogo globale</p>
    </div>

    <%
        List<Prenotazione> lista =
                (List<Prenotazione>) request.getAttribute("listaGlobalePrenotazioni");
    %>

    <% if (lista == null || lista.isEmpty()) { %>
    <div class="alert alert--success">
        Nessuna prenotazione presente nel sistema.
    </div>
    <% } else { %>

    <div class="table-container">
        <table class="aesthetic-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Data</th>
                <th>Stato</th>
                <th>Utente</th>
                <th>Postazione</th>
                <th>Ambiente</th>
            </tr>
            </thead>
            <tbody>
            <% for (Prenotazione p : lista) { %>
            <tr>
                <td class="id-badge">#<%= p.getIdPrenotazione() %></td>
                <td><strong><%= p.getData() %></strong></td>
                <td>
                    <span class="status-badge status-open"><%= p.getStato() %></span>
                </td>
                <td>ID: <%= p.getIdUtente() %></td>
                <td>Postazione <%= p.getIdPostazione() %></td>
                <td class="ambiente-text">
                    <%= (p.getNomeAmbiente() != null)
                            ? p.getNomeAmbiente()
                            : "N/D" %>
                </td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>

    <% } %>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos – Gestione Amministrativa
</footer>

</body>
</html>
