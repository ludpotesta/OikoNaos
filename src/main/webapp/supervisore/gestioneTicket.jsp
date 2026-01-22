<%@ page import="it.unisa.oikonaos.model.Ticket, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin - Gestione Ticket</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<jsp:include page="/include/header-navbar.jsp" />

<div class="container mt-5">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="text-primary">Gestione Globale Ticket</h2>
        <a href="${pageContext.request.contextPath}/dashboard-supervisore.jsp" class="btn btn-secondary">
            &larr; Torna alla Dashboard
        </a>
    </div>

    <% if ("updated".equals(request.getParameter("msg"))) { %>
    <div class="alert alert-success">Stato del ticket aggiornato con successo!</div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body">
            <table class="table table-striped table-hover align-middle">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Data</th>
                    <th>Titolo</th>
                    <th>Categoria</th>
                    <th>Priorità</th>
                    <th>Stato Attuale</th>
                    <th>Aggiorna Stato</th>
                    <th>Dettagli</th>
                </tr>
                </thead>
                <tbody>
                <%
                    // CORREZIONE 1: Uso "listaTicket" come definito nel Controller
                    List<Ticket> lista = (List<Ticket>) request.getAttribute("listaTicket");

                    if(lista != null && !lista.isEmpty()) {
                        for(Ticket t : lista) {
                            // Logica per colorare la priorità
                            String badgeColor = "bg-secondary";
                            if ("ALTA".equalsIgnoreCase(t.getPriorita())) badgeColor = "bg-danger";
                            else if ("MEDIA".equalsIgnoreCase(t.getPriorita())) badgeColor = "bg-warning text-dark";
                %>
                <tr>
                    <td>#<%= t.getIdTicket() %></td>
                    <td><%= t.getDataApertura() %></td>
                    <td class="fw-bold"><%= t.getTitolo() %></td>
                    <td><%= t.getCategoria() %></td>
                    <td><span class="badge <%= badgeColor %>"><%= t.getPriorita() %></span></td>

                    <td>
                        <% if("APERTO".equalsIgnoreCase(t.getStato())) { %>
                        <span class="badge bg-success">Aperto</span>
                        <% } else if("IN_LAVORAZIONE".equalsIgnoreCase(t.getStato())) { %>
                        <span class="badge bg-warning text-dark">In Lavorazione</span>
                        <% } else { %>
                        <span class="badge bg-secondary">Chiuso</span>
                        <% } %>
                    </td>

                    <td>
                        <form action="<%= request.getContextPath() %>/AdminTicketController" method="post" class="d-flex gap-2">
                            <input type="hidden" name="action" value="updateStato">
                            <input type="hidden" name="idTicket" value="<%= t.getIdTicket() %>">

                            <select name="stato" class="form-select form-select-sm" style="width: 130px;">
                                <option value="APERTO" <%= "APERTO".equalsIgnoreCase(t.getStato()) ? "selected" : "" %>>Aperto</option>
                                <option value="IN_LAVORAZIONE" <%= "IN_LAVORAZIONE".equalsIgnoreCase(t.getStato()) ? "selected" : "" %>>In Lavoraz.</option>
                                <option value="CHIUSO" <%= "CHIUSO".equalsIgnoreCase(t.getStato()) ? "selected" : "" %>>Chiuso</option>
                            </select>

                            <button type="submit" class="btn btn-sm btn-primary">OK</button>
                        </form>
                    </td>

                    <td>
                        <a href="<%= request.getContextPath() %>/AdminTicketController?action=dettagli&id=<%= t.getIdTicket() %>"
                           class="btn btn-sm btn-info text-white">
                            Info
                        </a>
                    </td>
                </tr>
                <%
                    }
                } else {
                %>
                <tr>
                    <td colspan="8" class="text-center p-4">Nessun ticket presente nel sistema.</td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
