<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Nuovo Ticket</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <!-- HEADER -->
    <header class="page-header">
        <h1 class="page-title">Apri una Segnalazione</h1>
        <p class="page-subtitle">
            Compila il modulo per inviare una richiesta di assistenza.
        </p>
    </header>

    <!-- CARD FORM -->
    <section class="card form-card">

        <form action="${pageContext.request.contextPath}/TicketController"
              method="post"
              enctype="multipart/form-data"
              class="form">

            <!-- TITOLO -->
            <div class="form-group">
                <label for="titolo">Titolo</label>
                <input type="text"
                       id="titolo"
                       name="titolo"
                       placeholder="Es. Problema con la postazione"
                       required>
            </div>

            <!-- DESCRIZIONE -->
            <div class="form-group">
                <label for="descrizione">Descrizione</label>
                <textarea id="descrizione"
                          name="descrizione"
                          rows="5"
                          placeholder="Descrivi il problema in modo dettagliato"
                          required></textarea>
            </div>

            <!-- CATEGORIA -->
            <div class="form-group">
                <label for="categoria">Categoria</label>
                <select id="categoria" name="categoria" required>
                    <option value="Hardware">Hardware</option>
                    <option value="Software">Software</option>
                </select>
            </div>

            <!-- PRIORITÀ -->
            <div class="form-group">
                <label for="priorita">Priorità</label>
                <select id="priorita" name="priorita" required>
                    <option value="BASSA">Bassa</option>
                    <option value="MEDIA">Media</option>
                    <option value="ALTA">Alta</option>
                </select>
            </div>

            <!-- ALLEGATI -->
            <div class="form-group">
                <label for="allegati">Allegati <span style="font-weight:400;">(max 5)</span></label>
                <input type="file"
                       id="allegati"
                       name="allegati"
                       multiple
                       accept=".pdf,.jpg,.png,.doc,.docx">
            </div>

            <!-- ACTIONS -->
            <div class="form-actions">
                <button type="submit" class="btn primary">
                    Invia Segnalazione
                </button>

                <a href="${pageContext.request.contextPath}/TicketController"
                   class="btn ghost">
                    Annulla
                </a>
            </div>

        </form>

    </section>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos - Ticket di Assistenza
</footer>

</body>
</html>
