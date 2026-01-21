<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<%@ page import="it.unisa.oikonaos.model.TassaTrimestrale" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dettagli Tassa</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp"/>

<main class="dashboard">

    <div class="page-header">
        <h1 class="title">Dettagli tassa</h1>
        <p class="page-subtitle">
            Stato dei pagamenti dei coinquilini
        </p>
    </div>

    <%
        TassaTrimestrale tassa =
                (TassaTrimestrale) request.getAttribute("tassa");

        List<Utente> coinquilini =
                (List<Utente>) request.getAttribute("coinquilini");

        List<Long> utentiPaganti =
                (List<Long>) request.getAttribute("utentiPaganti");
    %>

    <!-- INFO TASSA -->
    <section class="card" style="margin-bottom: 32px;">
        <h2 style="margin-top:0;">
            <%= tassa != null ? tassa.getTrimestreRiferimento() : "Tassa trimestrale" %>
        </h2>

        <p class="muted">
            Importo: <strong>€ <%= tassa != null ? tassa.getImportoDovuto() : "" %></strong><br>
            Scadenza: <%= tassa != null ? tassa.getScadenza() : "" %>
        </p>
    </section>

    <!-- LISTA COINQUILINI -->
    <section class="card">

        <h2>Coinquilini</h2>

        <table class="data-table">
            <thead>
            <tr>
                <th>Coinquilino</th>
                <th>Stato pagamento</th>
            </tr>
            </thead>

            <tbody>
            <%
                if (coinquilini != null && !coinquilini.isEmpty()) {
                    for (Utente u : coinquilini) {

                        boolean pagata =
                                utentiPaganti != null &&
                                        utentiPaganti.contains(u.getIdUtente());
            %>
            <tr>
                <td>
                    <%= u.getNome() %> <%= u.getCognome() %>
                </td>

                <td>
                    <% if (pagata) { %>
                    <span class="status status-paid">PAGATA</span>
                    <% } else { %>
                    <span class="status status-pending">DA PAGARE</span>
                    <% } %>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="2" class="empty-state">
                    Nessun coinquilino trovato
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>

    </section>

    <div style="margin-top: 24px;">
        <a href="${pageContext.request.contextPath}/SupervisoreTasseController"
           class="btn ghost">
            Torna alla gestione tasse
        </a>
    </div>

</main>

</body>
</html>

