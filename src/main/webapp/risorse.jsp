<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Risorse Condivise</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <style>
        .status-active {
            background: #e0f2fe;
            color: #0369a1;
        }

        .status-past {
            background: #f1f5f9;
            color: #64748b;
        }

        .status-pending {
            background: #fff7ed;
            color: #c2410c;
        }
    </style>
</head>
<body>

<jsp:include page="/include/header-navbar.jsp"/>

<main class="dashboard">

    <!-- RISORSE RICHIESTE -->
    <h1 class="page-title">Risorse Richieste</h1>

        <c:if test="${empty richiesteAttive}">
            <p>Nessuna richiesta attiva.</p>
        </c:if>

        <c:if test="${not empty richiesteAttive}">
            <div class="table-container" style="
                background: var(--card);
                border-radius: 18px;
                box-shadow: var(--shadow);
                padding: 24px;
                margin-top: 18px;
                overflow-x: auto;
        ">
                <table class="aesthetic-table" style="width:100%; border-collapse: collapse;">
                    <thead>
                    <tr>
                        <th style="text-align:left; padding:14px; color:var(--muted); font-size:11px; text-transform:uppercase; border-bottom:2px solid var(--bg);">
                            Risorsa
                        </th>
                        <th style="text-align:left; padding:14px; color:var(--muted); font-size:11px; text-transform:uppercase; border-bottom:2px solid var(--bg);">
                            Data
                        </th>
                        <th style="text-align:left; padding:14px; color:var(--muted); font-size:11px; text-transform:uppercase; border-bottom:2px solid var(--bg);">
                            Stato
                        </th>
                    </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="r" items="${richiesteAttive}">
                        <tr>
                            <!-- RISORSA -->
                            <td style="padding:16px; border-bottom:1px solid var(--bg); font-weight:700; color:var(--ink);">
                                    ${r.nomeRisorsa}
                            </td>

                            <!-- DATA -->
                            <td style="padding:16px; border-bottom:1px solid var(--bg);">
                                    ${r.dataInizio.toLocalDate()}
                            </td>

                            <!-- STATO -->
                            <td style="padding:16px; border-bottom:1px solid var(--bg);">
                                <span class="status
                                        ${r.stato == 'APPROVATA' ? 'status-active' :
                                          r.stato == 'RICHIESTA' ? 'status-pending' :
                                          'status-past'}"
                                                              style="
                                              display:inline-block;
                                              padding:6px 12px;
                                              border-radius:999px;
                                              font-size:11px;
                                              font-weight:800;
                                          ">
                                                                ${r.stato}
                                </span>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>
    </section>

    <!-- RISORSE DISPONIBILI -->
    <h2 class="page-title">Risorse Disponibili</h2>
    <c:if test="${empty risorseDisponibili}">
        <p>Nessuna risorsa disponibile.</p>
    </c:if>

    <c:forEach var="r" items="${risorseDisponibili}">
        <div class="dashboard-card active">
            <h3>${r.nome}</h3>
            <p>${r.descrizione}</p>

            <!-- REGOLE D'USO -->
            <p class="regole-uso">
                <strong>Regole d’uso:</strong><br>
                    ${r.regoleUso}<br>
                <em>Penale in caso di infrazione: € ${r.penale}</em>
            </p>

            <!-- FORM RICHIESTA -->
            <form action="RisorsaController" method="post" style="margin-top: 16px;">

                <input type="hidden" name="action" value="richiedi">
                <input type="hidden" name="idRisorsa" value="${r.idRisorsa}">

                <!-- DATA -->
                <div style="margin-bottom: 12px;">
                    <label style="font-weight:600; font-size:0.9rem;">
                        Data
                    </label><br>
                    <input type="date"
                           name="data"
                           class="date-picker"
                           data-risorsa="${r.idRisorsa}"
                           data-occupate="${dateOccupate[r.idRisorsa]}"
                           min="${oggi}"
                           style="
                                   margin-top:6px;
                                   padding:8px 12px;
                                   border-radius:10px;
                                   border:1px solid #d1d5db;
                                   font-family:inherit;
                               ">
                </div>

                <!-- CHECKBOX REGOLE -->
                <div style="margin-bottom: 18px;">
                    <label style="display:flex; align-items:flex-start; gap:8px; cursor:pointer;">
                        <input type="checkbox" name="accettaRegole" required>
                        <span>
                Accetto le <strong>regole d’uso</strong>
            </span>
                    </label>
                </div>

                <!-- BOTTONE -->
                <button type="submit" class="btn primary">
                    Richiedi
                </button>

            </form>
        </div>
    </c:forEach>

</main>
</body>

<script>
    document.querySelectorAll('.date-picker').forEach(input => {
        const occupate = input.dataset.occupate
            ?.replace(/[\[\]\s]/g, '')
            .split(',')
            .filter(Boolean);

        input.addEventListener('input', () => {
            if (occupate.includes(input.value)) {
                alert("Questa risorsa è già prenotata per il giorno selezionato.");
                input.value = '';
            }
        });
    });
</script>
</html>
