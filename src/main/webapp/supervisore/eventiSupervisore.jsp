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

        <!-- BLOCCO CONFERMA CANCELLAZIONE (COME PRENOTAZIONI) -->
        <div id="deleteEventBox" class="confirm-box" style="display:none;">

            <h3>Conferma cancellazione</h3>
            <p>
                Sei sicuro di voler cancellare l’evento
                <strong id="deleteEventTitle"></strong>?
            </p>

            <form action="${pageContext.request.contextPath}/SupervisoreEventiController"
                  method="post">

                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="idEvento" id="deleteEventId">

                <div class="confirm-actions">
                    <button type="button"
                            class="btn secondary"
                            onclick="closeDeleteBox()">
                        No
                    </button>

                    <button type="submit"
                            class="btn primary">
                        Sì, cancella
                    </button>
                </div>
            </form>
        </div>

        <!-- AZIONE NUOVO EVENTO -->
        <div style="margin-bottom:20px;">
            <a class="btn primary"
               href="${pageContext.request.contextPath}/SupervisoreEventiController?action=new">
                + Nuovo evento
            </a>
        </div>

        <!-- TABELLA EVENTI -->
        <table class="data-table">
            <thead>
            <tr>
                <th>Titolo</th>
                <th>Data</th>
                <th>Luogo</th>
                <th>Posti</th>
                <th>Stato</th>
                <th>Azioni</th>
            </tr>
            </thead>

            <tbody>
            <%
                List<Object[]> eventi =
                        (List<Object[]>) request.getAttribute("eventi");

                if (eventi == null || eventi.isEmpty()) {
            %>
            <tr>
                <td colspan="6" style="text-align:center; color:var(--muted);">
                    Nessun evento presente.
                </td>
            </tr>
            <%
            } else {
                for (Object[] e : eventi) {
                    String stato = (String) e[5];
                    boolean terminato = "Passato".equalsIgnoreCase(stato);
            %>
            <tr>
                <td><strong><%= e[1] %></strong></td>
                <td><%= e[2] %></td>
                <td><%= e[3] %></td>
                <td><%= e[4] %></td>

                <td>
                    <% if (terminato) { %>
                    <span class="status terminated">TERMINATO</span>
                    <% } else { %>
                    <span class="status active">ATTIVO</span>
                    <% } %>
                </td>

                <td>
                    <% if (!terminato) { %>
                    <button type="button"
                            class="btn btn-delete"
                            onclick="openDeleteBox(<%= e[0] %>, '<%= e[1] %>')">
                        Cancella
                    </button>
                    <% } else { %>
                    <span style="color:var(--muted);">—</span>
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

<script>
    function openDeleteBox(idEvento, titolo) {
        document.getElementById('deleteEventId').value = idEvento;
        document.getElementById('deleteEventTitle').innerText = titolo;
        document.getElementById('deleteEventBox').style.display = 'block';
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    function closeDeleteBox() {
        document.getElementById('deleteEventBox').style.display = 'none';
    }
</script>
