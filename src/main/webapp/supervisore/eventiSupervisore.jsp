<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <header class="page-header">
        <h1 class="page-title">Gestione Eventi</h1>
        <p class="page-subtitle">
            Elenco completo degli eventi della comunità
        </p>
    </header>

    <div class="card">

        <div style="margin-bottom:20px;">
            <a class="btn primary"
               href="${pageContext.request.contextPath}/SupervisoreEventiController?action=new">
                + Nuovo evento
            </a>
        </div>

        <table class="data-table">
            <thead>
            <tr>
                <th>Titolo</th>
                <th>Data</th>
                <th>Luogo</th>
                <th>Posti</th>
                <th>Stato</th>
            </tr>
            </thead>

            <tbody>
            <%
                List<Object[]> eventi =
                        (List<Object[]>) request.getAttribute("eventi");

                if (eventi == null || eventi.isEmpty()) {
            %>
            <tr>
                <td colspan="5" style="text-align:center; color:var(--muted);">
                    Nessun evento presente.
                </td>
            </tr>
            <%
            } else {
                for (Object[] e : eventi) {
            %>
            <tr>
                <td><strong><%= e[1] %></strong></td>
                <td><%= e[2] %></td>
                <td><%= e[3] %></td>
                <td><%= e[4] %></td>
                <td>
                    <%
                        String stato = (String) e[5];
                        boolean terminato = "Passato".equalsIgnoreCase(stato);
                    %>

                    <% if (terminato) { %>
                    <span class="status terminated">TERMINATO</span>
                    <% } else { %>
                    <span class="status active">ATTIVO</span>
                    <% } %>
                </td>

            </tr>
            <%
                    }
                }
            %>
            </tbody>
        </table>

    </div>

</main>

