<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="it.unisa.oikonaos.model.TassaTrimestrale, java.util.List" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Tasse - Supervisore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        .card {
            background: var(--card);
            border-radius: 18px;
            box-shadow: var(--shadow);
            padding: 28px;
            margin-bottom: 32px;
        }

        .card h2 {
            margin-top: 0;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
            margin-top: 20px;
        }

        .form-grid input {
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-family: inherit;
        }

        .btn-primary {
            background: var(--brand);
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 12px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s, filter 0.2s;
        }

        .btn-primary:hover {
            filter: brightness(0.9);
            transform: scale(1.03);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 16px;
        }

        th {
            text-align: left;
            font-size: 12px;
            text-transform: uppercase;
            color: var(--muted);
            padding: 12px;
            border-bottom: 2px solid var(--bg);
        }

        td {
            padding: 14px 12px;
            border-bottom: 1px solid var(--bg);
        }

        .status {
            padding: 6px 12px;
            border-radius: 999px;
            font-size: 11px;
            font-weight: 700;
            display: inline-block;
            background: #f1f5f9;
            color: #475569;
        }

        .status-active {
            background: #ecfeff;
            color: #0369a1;
        }

        .status-expired {
            background: #fef2f2;
            color: #991b1b;
        }
    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp"/>

<main class="page">

    <div class="page-header">
        <h1 class="title">Gestione Tasse</h1>
        <p class="page-subtitle">Backoffice amministrativo</p>
    </div>

    <!-- INSERIMENTO NUOVA TASSA -->
    <div class="card">
        <h2>Inserisci nuova tassa</h2>

        <form method="post"
              action="${pageContext.request.contextPath}/SupervisoreTasseController">

            <div class="form-grid">
                <input type="text"
                       name="trimestre"
                       placeholder="Trimestre (es. Q1 2026)"
                       required>

                <input type="number"
                       name="importo"
                       step="0.01"
                       placeholder="Importo (€)"
                       required>

                <input type="date"
                       name="scadenza"
                       required>
            </div>

            <div style="margin-top:20px;">
                <button type="submit" class="btn-primary">
                    Crea tassa
                </button>
            </div>
        </form>
    </div>

    <!-- LISTA TASSE -->
    <div class="card">
        <h2>Tasse esistenti</h2>

        <%
            List<TassaTrimestrale> tasse =
                    (List<TassaTrimestrale>) request.getAttribute("tasse");
        %>

        <% if (tasse != null && !tasse.isEmpty()) { %>

        <table>
            <thead>
            <tr>
                <th>Trimestre</th>
                <th>Importo</th>
                <th>Scadenza</th>
                <th>Stato</th>
            </tr>
            </thead>
            <tbody>

            <% for (TassaTrimestrale t : tasse) { %>
            <tr>
                <td><%= t.getTrimestreRiferimento() %></td>
                <td>€ <%= String.format("%.2f", t.getImportoDovuto()) %></td>
                <td><%= t.getScadenza() %></td>
                <%
                    boolean scaduta = t.getScadenza().isBefore(java.time.LocalDate.now());
                    String stato = scaduta ? "SCADUTA" : "ATTIVA";
                %>
                <td>
                    <span class="status <%= scaduta ? "status-expired" : "status-active" %>">
                        <%= stato %>
                    </span>
                </td>
            </tr>
            <% } %>

            </tbody>
        </table>

        <% } else { %>

        <p class="muted">Nessuna tassa presente nel sistema.</p>

        <% } %>
    </div>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos – Backoffice
</footer>

</body>
</html>
