<%@ page contentType="text/html; charset=UTF-8" %>

<jsp:include page="/include/header-navbar.jsp" />

<main class="page">

    <header class="page-header">
        <h1 class="page-title">Nuovo Evento</h1>
        <p class="page-subtitle">
            Inserisci i dettagli del nuovo evento
        </p>
    </header>

    <div class="card">

        <form method="post"
              action="${pageContext.request.contextPath}/SupervisoreEventiController"
              class="form-grid">

            <input type="hidden" name="action" value="create"/>

            <!-- TITOLO -->
            <div class="form-group full">
                <label for="titolo">Titolo</label>
                <input type="text" id="titolo" name="titolo" required>
            </div>

            <!-- DESCRIZIONE -->
            <div class="form-group full">
                <label for="descrizione">Descrizione</label>
                <textarea id="descrizione" name="descrizione" rows="3"></textarea>
            </div>

            <!-- LUOGO -->
            <div class="form-group">
                <label for="luogo">Luogo</label>
                <input type="text" id="luogo" name="luogo">
            </div>

            <!-- POSTI TOTALI -->
            <div class="form-group">
                <label for="posti">Posti totali</label>
                <input type="number" id="posti" name="posti" min="1" required>
            </div>

            <!-- DATA ORA INIZIO -->
            <div class="form-group">
                <label for="inizio">Data e ora inizio</label>
                <input type="datetime-local" id="inizio" name="dataInizio" required>
            </div>

            <!-- DATA ORA FINE -->
            <div class="form-group">
                <label for="fine">Data e ora fine</label>
                <input type="datetime-local" id="fine" name="dataFine">
            </div>

            <!-- AZIONI -->
            <div class="form-actions full">
                <button type="submit" class="btn primary">
                    Crea evento
                </button>

                <a href="${pageContext.request.contextPath}/SupervisoreEventiController"
                   class="btn ghost">
                    Annulla
                </a>
            </div>

        </form>

    </div>

</main>
