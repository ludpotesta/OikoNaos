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

    <section class="table-container">

        <table class="aesthetic-table">
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

                <td class="amount">
                    € <%= p.getImportoPagato() %>
                </td>

                <td>
                    <% if (p.isPagato()) { %>
                    <span class="status-badge paid">Pagato</span>
                    <% } else { %>
                    <span class="status-badge pending">Da pagare</span>
                    <% } %>
                </td>

                <td>
                    <% if (!p.isPagato()) { %>
                    <form method="post"
                          action="${pageContext.request.contextPath}/SpeseController"
                          style="display:inline;">
                        <input type="hidden" name="action" value="pay">
                        <input type="hidden" name="idPagamento"
                               value="<%= p.getIdPagamento() %>">
                        <button type="submit" class="btn-primary">
                            Paga ora
                        </button>
                    </form>
                    <% } else { %>
                    <a href="${pageContext.request.contextPath}/RicevutaController?idPagamento=<%= p.getIdPagamento() %>"
                       class="btn-secondary">
                        Ricevuta
                    </a>
                    <% } %>
                </td>
            </tr>
            <%
                }
            } else {
            %>
            <tr>
                <td colspan="4" class="empty-row">
                    Nessuna spesa disponibile
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>

    </section>
</main>

</body>
</html>
