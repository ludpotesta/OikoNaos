<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Pagamento" %>

<%
    Pagamento p = (Pagamento) request.getAttribute("pagamento");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Pagamento spesa</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="dashboard">

    <h1 class="page-title">Pagamento spesa</h1>

    <section class="dashboard-card">

        <p><strong>Periodo:</strong> <%= p.getPeriodo() %></p>
        <p><strong>Importo:</strong> € <%= p.getImportoPagato() %></p>

        <form method="post"
              action="${pageContext.request.contextPath}/SpeseController">

            <input type="hidden" name="action" value="pay">
            <input type="hidden" name="idPagamento"
                   value="<%= p.getIdPagamento() %>">

            <label class="section-title">Metodo di pagamento</label>

            <div class="form-group">
                <label>
                    <input type="radio" name="metodo" value="ONLINE" checked>
                    Carta / Online
                </label><br>

                <label>
                    <input type="radio" name="metodo" value="BONIFICO">
                    Bonifico
                </label><br>

                <label>
                    <input type="radio" name="metodo" value="CONTANTI">
                    Contanti
                </label>
            </div>

            <button type="submit" class="btn primary">
                Conferma pagamento
            </button>

            <a href="${pageContext.request.contextPath}/SpeseController"
               class="btn ghost">
                Annulla
            </a>
        </form>

    </section>
</main>

</body>
</html>
