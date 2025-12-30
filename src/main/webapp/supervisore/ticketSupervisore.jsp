<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Ticket - OikoNaos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        /* Container tabella coerente con design system */
        .table-container {
            background: var(--card);
            border-radius: 18px;
            box-shadow: var(--shadow);
            padding: 24px;
            margin-top: 30px;
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

        /* Badge stato ticket */
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

        /* Form azioni */
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

    <!-- HEADER PAGINA (stesso stile di coinquilino e prenotazioni supervisore) -->
    <div class="page-header">
        <h1 class="title">Gestione Ticket</h1>
        <p class="page-subtitle">Supporto e segnalazioni</p>
    </div>

    <%
        List<Ticket> lista = (List<Ticket>) request.getAttribute("listaGlobaleTicket");
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

                <td><%= t.getTitolo() %></td>

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

                        <button type="submit" class="btn-action">
                            Aggiorna
                        </button>
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
