<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

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

        .form-grid input,
        .form-grid select,
        select {
            padding: 10px 14px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-family: inherit;
            font-size: 0.95rem;
        }

        .btn-primary {
            background: var(--brand);
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 12px;
            font-weight: 600;
            cursor: pointer;
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

    <!-- FORM INSERIMENTO TASSA -->
    <div class="card">
        <h2>Inserisci nuova tassa</h2>

        <form method="post"
              action="${pageContext.request.contextPath}/SupervisoreTasseController">

            <div class="form-grid">
                <select name="tipo" required>
                    <option value="ORDINARIA">Tassa ordinaria (trimestrale)</option>
                    <option value="STRAORDINARIA">Tassa straordinaria / penale</option>
                </select>

                <input type="text" name="trimestre"
                       placeholder="Trimestre (es. Q1 2026)" required>

                <input type="number" name="importo" step="0.01"
                       placeholder="Importo (€)" required>

                <input type="date" name="scadenza" required>
            </div>

            <div style="margin-top: 18px;">
                <label><strong>Destinatari</strong></label><br>
                <label>
                    <input type="radio" name="destinatario" value="TUTTI" checked>
                    Tutti i coinquilini
                </label>
                <label style="margin-left: 20px;">
                    <input type="radio" name="destinatario" value="SINGOLO">
                    Coinquilino specifico
                </label>
            </div>

            <div style="margin-top: 12px;">
                <select name="idUtente">
                    <option value="">— Seleziona coinquilino (opzionale) —</option>
                    <c:forEach var="u" items="${coinquilini}">
                        <option value="${u.idUtente}">
                                ${u.nome} ${u.cognome}
                        </option>
                    </c:forEach>
                </select>
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

        <c:choose>
            <c:when test="${not empty tasse}">
                <table>
                    <thead>
                    <tr>
                        <th>Tassa</th>
                        <th>Importo</th>
                        <th>Scadenza</th>
                        <th>Destinatari</th>
                        <th>Stato</th>
                    </tr>
                    </thead>
                    <tbody>

                    <c:forEach var="t" items="${tasse}">
                        <tr>
                            <td>
                                <c:choose>
                                    <c:when test="${empty t.idUtente}">
                                        <a href="${pageContext.request.contextPath}/SupervisoreDettagliTassaController?idTassa=${t.idTassa}">
                                                ${t.trimestreRiferimento}
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        ${t.trimestreRiferimento}
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>€ ${t.importoDovuto}</td>

                            <td>${t.scadenza}</td>

                            <td>
                                <c:choose>
                                    <c:when test="${empty t.idUtente}">
                                        Tutti i coinquilini
                                    </c:when>
                                    <c:otherwise>
                                        ${t.nomeUtente} ${t.cognomeUtente}
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${not empty t.idUtente}">
                                        <span class="status ${t.pagata ? 'status-active' : 'status-expired'}">
                                                ${t.pagata ? 'PAGATA' : 'NON PAGATA'}
                                        </span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="status ${t.scaduta ? 'status-expired' : 'status-active'}">
                                                ${t.scaduta ? 'SCADUTA' : 'ATTIVA'}
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>

                    </tbody>
                </table>
            </c:when>

            <c:otherwise>
                <p class="muted">Nessuna tassa presente nel sistema.</p>
            </c:otherwise>
        </c:choose>

    </div>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos – Backoffice
</footer>

</body>
</html>
