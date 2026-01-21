<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Gestione Risorse</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        .table {
            width: 100%;
            border-collapse: collapse;
        }

        .table th,
        .table td {
            vertical-align: top;
            padding: 12px 14px;
            line-height: 1.4;
        }

        .table th {
            text-align: left;
            font-weight: 600;
            color: var(--ink);
            border-bottom: 1px solid #e5e7eb;
        }

        .table td {
            border-bottom: 1px solid #f1f5f9;
        }

        .table th.penale,
        .table td.penale {
            width: 90px;
            text-align: right;
            white-space: nowrap;
        }

        .table td.regole {
            max-width: 420px;
            white-space: normal;
        }

        .rules {
            margin: 0;
            padding-left: 16px;
        }

        .rules li {
            margin-bottom: 6px;
        }

        .status-badge {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 999px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
        }

        .status-richiesta {
            background: #fef3c7;
            color: #92400e;
        }

        .status-approvata {
            background: #dcfce7;
            color: #166534;
        }

        .status-rifiutata {
            background: #fee2e2;
            color: #991b1b;
        }

        .action-buttons {
            display: flex;
            gap: 8px;
        }

        .btn-sm {
            padding: 6px 12px;
            font-size: 0.8rem;
            border-radius: 8px;
        }

        .btn-success {
            background: #22c55e;
            color: white;
        }

        .btn-danger {
            background: #ef4444;
            color: white;
        }

        .table td.actions {
            white-space: nowrap;
        }

        .btn-primary {
            background: #3b82f6;
            color: white;
        }

    </style>
</head>

<body>

<jsp:include page="/include/header-navbar.jsp"/>

<main class="dashboard">

    <h1>Gestione Risorse</h1>
    <p class="subtitle">
        Inserisci nuove risorse e gestisci le richieste dei coinquilini.
    </p>

    <!-- ================= INSERIMENTO RISORSA ================= -->
    <section class="card">
        <h2>Nuova Risorsa</h2>

        <form method="post"
              action="${pageContext.request.contextPath}/SupervisoreRisorseController"
              class="form">

            <input type="hidden" name="action" value="inserisciRisorsa"/>

            <div class="form-group">
                <label>Nome</label>
                <input type="text" name="nome" required>
            </div>

            <div class="form-group">
                <label>Descrizione</label>
                <textarea name="descrizione" rows="3"></textarea>
            </div>

            <div class="form-group">
                <label>Regole d’uso</label>
                <textarea name="regoleUso" rows="3"></textarea>
            </div>

            <div class="form-group">
                <label>Penale (€)</label>
                <input type="number" name="penale" step="0.01">
            </div>

            <button type="submit" class="btn btn-primary">
                Inserisci risorsa
            </button>
        </form>
    </section>

    <!-- ================= LISTA RISORSE ================= -->
    <section class="card">
        <h2>Risorse presenti</h2>

        <table class="table">
            <thead>
            <tr>
                <th>Nome</th>
                <th>Descrizione</th>
                <th>Regole d’uso</th>
                <th class="penale">Penale</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="r" items="${risorse}">
                <tr>
                    <td><strong>${r.nome}</strong></td>
                    <td>${r.descrizione}</td>
                    <td class="regole">
                        <ul class="rules">
                            <c:forEach var="riga" items="${fn:split(r.regoleUso, '•')}">
                                <c:if test="${not empty riga}">
                                    <li>${riga}</li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </td>
                    <td class="penale">
                        <c:choose>
                            <c:when test="${r.penale != null}">
                                € ${r.penale}
                            </c:when>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>

    <!-- ================= RICHIESTE RISORSE ================= -->
    <section class="card">
        <h2>Richieste dei coinquilini</h2>

        <c:if test="${empty richieste}">
            <p class="muted">
                Nessuna richiesta presente al momento.
            </p>
        </c:if>

        <table class="table">
            <thead>
            <tr>
                <th>Risorsa</th>
                <th>Utente</th>
                <th>Periodo</th>
                <th>Stato</th>
                <th>Azioni</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="req" items="${richieste}">
                <tr>
                    <td>${req.nomeRisorsa}</td>
                    <td>${req.nomeUtente}</td>
                    <td>
                            ${req.dataInizioFormatted}
                        <br>
                        →
                            ${req.dataFineFormatted}
                    </td>
                    <td>
                        <span class="status-badge status-${fn:toLowerCase(req.stato)}">
                                ${req.stato}
                        </span>
                    </td>

                    <td class="actions">
                        <c:if test="${req.stato eq 'RICHIESTA'}">
                            <div class="action-buttons">
                                <form method="post"
                                      action="${pageContext.request.contextPath}/SupervisoreRisorseController">
                                    <input type="hidden" name="action" value="accettaRichiesta">
                                    <input type="hidden" name="idRichiesta" value="${req.idRichiesta}">
                                    <button class="btn btn-success btn-sm" type="submit">
                                        Approva
                                    </button>
                                </form>

                                <form method="post"
                                      action="${pageContext.request.contextPath}/SupervisoreRisorseController">
                                    <input type="hidden" name="action" value="rifiutaRichiesta">
                                    <input type="hidden" name="idRichiesta" value="${req.idRichiesta}">
                                    <button class="btn btn-danger btn-sm" type="submit">
                                        Rifiuta
                                    </button>
                                </form>
                            </div>
                        </c:if>
                        <c:if test="${req.stato ne 'RICHIESTA'}">
                            —
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>

</main>

</body>
</html>
