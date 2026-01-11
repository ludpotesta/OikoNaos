<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="it.unisa.oikonaos.model.Pagamento" %>

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
    List<Pagamento> pagamenti =
            (List<Pagamento>) request.getAttribute("pagamenti");
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
                    if (pagamenti != null && !pagamenti.isEmpty()) {
                        for (Pagamento p : pagamenti) {
                %>
                <tr>
                    <td><%= p.getPeriodo() %></td>

                    <td><strong>€ <%= p.getImportoPagato() %></strong></td>

                    <td>
                        <% if (p.isPagato()) { %>
                        <span class="status status-paid">PAGATA</span>
                        <% } else if (p.isScaduta()) { %>
                        <span class="status status-expired">SCADUTA</span>
                        <% } else { %>
                        <span class="status status-pending">DA PAGARE</span>
                        <% } %>
                    </td>

                    <td>
                        <% if (!p.isPagato()) { %>
                        <form method="get"
                              action="${pageContext.request.contextPath}/SpeseController"
                              style="display:inline;">

                            <input type="hidden" name="action" value="confirm">
                            <input type="hidden" name="idPagamento"
                                   value="<%= p.getIdPagamento() %>">

                            <button type="submit" class="btn ghost">
                                Paga
                            </button>
                        </form>
                        <% } else { %>
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
