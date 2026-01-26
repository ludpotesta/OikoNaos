<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Ticket - OikoNaos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        .ticket-filters {
            display: flex;
            gap: 14px;
            flex-wrap: wrap;
            align-items: center;
            background: var(--card);
            padding: 20px;
            border-radius: 18px;
            box-shadow: var(--shadow);
            border: 1px solid rgba(0,0,0,0.05);
            margin-top: 24px;
            margin-bottom: 30px;
        }

        .ticket-filters select,
        .ticket-filters input {
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-family: inherit;
            font-size: 14px;
            background: #fff;
            color: var(--ink);
        }

        .ticket-filters button {
            background: var(--brand);
            color: #fff;
            border: none;
            padding: 10px 18px;
            border-radius: 12px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, filter 0.2s;
        }

        .ticket-filters button:hover {
            filter: brightness(0.9);
            transform: scale(1.03);
        }

        /* ===== TABELLA ===== */
        .table-container {
            background: var(--card);
            border-radius: 18px;
            box-shadow: var(--shadow);
            padding: 24px;
            border: 1px solid rgba(0,0,0,0.05);
            overflow-x: auto;
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
            font-size: 11px;
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

        .form-select {
            padding: 8px 12px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            background: #fff;
            font-family: inherit;
            color: var(--ink);
            cursor: pointer;
        }

        .btn-action {
            background: var(--brand);
            color: #fff;
            border: none;
            padding: 8px 16px;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, filter 0.2s;
        }

        .btn-action:hover {
            filter: brightness(0.9);
            transform: scale(1.02);
        }
    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <div class="page-header">
        <h1 class="title">Gestione Ticket</h1>
        <p class="page-subtitle">Supporto e segnalazioni</p>
    </div>

    <!-- FILTRI -->
    <form method="get"
          action="${pageContext.request.contextPath}/SupervisoreTicketController"
          class="ticket-filters">

        <select name="stato">
            <option value="">Tutti gli stati</option>
            <option value="APERTO" ${param.stato == 'APERTO' ? 'selected' : ''}>Aperto</option>
            <option value="IN_LAVORAZIONE" ${param.stato == 'IN_LAVORAZIONE' ? 'selected' : ''}>
                In lavorazione
            </option>
            <option value="CHIUSO" ${param.stato == 'CHIUSO' ? 'selected' : ''}>Chiuso</option>
        </select>

        <select name="priorita">
            <option value="">Tutte le priorità</option>
            <option value="BASSA" ${param.priorita == 'BASSA' ? 'selected' : ''}>Bassa</option>
            <option value="MEDIA" ${param.priorita == 'MEDIA' ? 'selected' : ''}>Media</option>
            <option value="ALTA" ${param.priorita == 'ALTA' ? 'selected' : ''}>Alta</option>
        </select>

        <input type="date" name="dataCreazione" value="${param.dataCreazione}">

        <button type="submit">Filtra</button>
    </form>

    <%
        List<Ticket> lista = (List<Ticket>) request.getAttribute("listaTicket");
    %>

    <div class="table-container">
        <table class="aesthetic-table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Titolo</th>
                <th>Stato attuale</th>
                <th>Azione</th>
            </tr>
            </thead>
            <tbody>

            <% if (lista != null && !lista.isEmpty()) {
                for (Ticket t : lista) {
                    String statusClass =
                            t.getStato().equalsIgnoreCase("CHIUSO")
                                    ? "status-closed"
                                    : "status-open";
            %>

            <tr>
                <td style="font-weight:800; color:var(--brand);">
                    #<%= t.getIdTicket() %>
                </td>

                <td>
                    <a href="<%= request.getContextPath() %>/SupervisoreTicketController?action=details&idTicket=<%= t.getIdTicket() %>"
                       class="link-primary">
                        <%= t.getTitolo() %>
                    </a>
                </td>

                <td>
                    <span class="status-badge <%= statusClass %>">
                        <%= t.getStato().replace("_", " ") %>
                    </span>
                </td>

                <td>
                    <form method="post"
                          action="<%= request.getContextPath() %>/SupervisoreTicketController"
                          style="display:flex; gap:10px; align-items:center;">

                        <input type="hidden" name="action" value="updateStato">
                        <input type="hidden" name="idTicket" value="<%= t.getIdTicket() %>">

                        <select name="nuovoStato" class="form-select">
                            <option value="IN_LAVORAZIONE"
                                    <%= t.getStato().equals("IN_LAVORAZIONE") ? "selected" : "" %>>
                                In lavorazione
                            </option>
                            <option value="CHIUSO"
                                    <%= t.getStato().equals("CHIUSO") ? "selected" : "" %>>
                                Chiuso
                            </option>
                        </select>

                        <button type="submit" class="btn-action">Aggiorna</button>
                    </form>
                </td>
            </tr>

            <% } } else { %>

            <tr>
                <td colspan="4" class="empty-state">
                    Nessun ticket presente nel sistema.
                </td>
            </tr>

            <% } %>

            </tbody>
        </table>
    </div>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos – Helpdesk Management
</footer>

</body>
</html>
