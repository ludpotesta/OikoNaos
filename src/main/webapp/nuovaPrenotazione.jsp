<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Nuova Prenotazione</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <!-- HEADER -->
    <header class="page-header">
        <h1 class="page-title">Nuova Prenotazione</h1>
        <p class="page-subtitle">
            Seleziona data, ambiente e fascia oraria per prenotare una postazione.
        </p>
    </header>

    <!-- CARD FORM -->
    <section class="card form-card">

        <%-- ERRORI --%>
        <%
            String error = request.getParameter("error");
            if ("conflitto".equals(error)) {
        %>
        <div class="alert alert--danger">
            Postazione già occupata in questa fascia oraria.
        </div>
        <%
        } else if ("data_passata".equals(error)) {
        %>
        <div class="alert alert--danger">
            Non puoi prenotare una data passata.
        </div>
        <%
            }
        %>

        <form action="${pageContext.request.contextPath}/PrenotazioneController"
              method="post"
              class="form">

            <input type="hidden" name="action" value="create">

            <!-- DATA -->
            <div class="form-group">
                <label for="data">Data</label>
                <input type="date"
                       id="data"
                       name="data"
                       min="<%= java.time.LocalDate.now() %>"
                       required>
            </div>

            <!-- AMBIENTE -->
            <div class="form-group">
                <label for="ambiente">Ambiente</label>
                <select id="ambiente" name="ambiente" required>
                    <option value="">-- Seleziona ambiente --</option>
                    <option value="1">Sala Studio</option>
                    <option value="2">Palestra</option>
                </select>
            </div>

            <!-- POSTAZIONE -->
            <div class="form-group">
                <label for="idPostazione">Postazione</label>
                <select id="idPostazione" name="idPostazione" required>
                    <option value="">-- Seleziona postazione --</option>
                    <option value="1">Postazione 1</option>
                    <option value="2">Postazione 2</option>
                    <option value="3">Postazione 3</option>
                </select>
            </div>

            <!-- FASCIA ORARIA -->
            <div class="form-group">
                <label for="idFascia">Fascia oraria</label>
                <select id="idFascia" name="idFascia" required>
                    <option value="">-- Seleziona fascia oraria --</option>
                    <option value="1">08:00 - 12:00</option>
                    <option value="2">12:00 - 15:00</option>
                    <option value="3">15:00 - 18:00</option>
                </select>
            </div>

            <!-- ACTIONS -->
            <div class="form-actions">
                <button type="submit" class="btn primary">
                    Conferma prenotazione
                </button>

                <a href="${pageContext.request.contextPath}/PrenotazioneController?action=list"
                   class="btn ghost">
                    Annulla
                </a>
            </div>

        </form>

    </section>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos - Prenotazioni
</footer>

</body>
</html>
