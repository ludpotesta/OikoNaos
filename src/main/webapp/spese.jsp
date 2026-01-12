<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unisa.oikonaos.model.TassaTrimestrale" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Spese</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<%
    List<TassaTrimestrale> tasse =
            (List<TassaTrimestrale>) request.getAttribute("tasse");
%>

<main class="dashboard">

    <h1 class="page-title">Le mie spese</h1>

    <section class="card">

        <div class="table-wrapper">
            <table class="data-table">
                <thead>
                <tr>
                    <th>Periodo</th>
                    <th>Importo</th>
                    <th>Stato</th>
                    <th>Azione</th>
                </tr>
                </thead>

                <tbody>
                <%
                    if (tasse != null && !tasse.isEmpty()) {
                        for (TassaTrimestrale t : tasse) {
                %>
                <tr>
                    <td><%= t.getTrimestreRiferimento() %></td>

                    <td>
                        <strong>€ <%= t.getImportoDovuto() %></strong>
                    </td>

                    <td>
                        <% if (t.isPagata()) { %>
                        <span class="status status-paid">PAGATA</span>
                        <% } else { %>
                        <span class="status status-pending">DA PAGARE</span>
                        <% } %>
                    </td>

                    <td>
                        <% if (!t.isPagata()) { %>

                        <!-- TASSA NON PAGATA -->
                        <form method="post"
                              action="${pageContext.request.contextPath}/SpeseController"
                              style="display:inline;">

                            <input type="hidden" name="action" value="startPay">
                            <input type="hidden" name="idTassa" value="<%= t.getIdTassa() %>">

                            <button type="submit" class="btn ghost">
                                Paga
                            </button>
                        </form>

                        <% } else if (t.hasRicevuta()) { %>

                        <!-- TASSA PAGATA + RICEVUTA -->
                        <a href="${pageContext.request.contextPath}/RicevutaController?idPagamento=<%= t.getIdPagamento() %>"
                           class="btn ghost">
                            Ricevuta
                        </a>

                        <% } else { %>

                        <!-- TASSA PAGATA MA SENZA RICEVUTA -->
                        –

                        <% } %>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr>
                    <td colspan="4" class="empty-state">
                        Nessuna spesa disponibile
                    </td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>

    </section>
</main>

</body>
</html>
