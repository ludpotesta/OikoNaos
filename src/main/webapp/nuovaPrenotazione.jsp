<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Nuova Prenotazione</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp"/>

<main class="page">

    <header class="page-header">
        <h1 class="page-title">Nuova Prenotazione</h1>
        <p class="page-subtitle">
            Seleziona data, ambiente, postazione e fascia oraria.
        </p>
    </header>

    <section class="card form-card">

        <form action="${pageContext.request.contextPath}/PrenotazioneController"
              method="post" class="form">

            <input type="hidden" name="action" value="create"/>

            <!-- DATA -->
            <div class="form-group">
                <label>Data</label>
                <input type="date"
                       name="data"
                       min="<%= java.time.LocalDate.now() %>"
                       required/>
            </div>

            <!-- AMBIENTE -->
            <div class="form-group">
                <label>Ambiente</label>
                <select id="ambiente" name="idAmbiente" required>
                    <option value="">-- Seleziona ambiente --</option>
                    <option value="1">Sala Studio</option>
                    <option value="2">Palestra</option>
                    <option value="5">Sala Coworking</option>
                    <option value="6">Sala Relax</option>
                    <option value="7">Cucina Condivisa</option>
                </select>
            </div>

            <!-- POSTAZIONE -->
            <div class="form-group">
                <label>Postazione</label>
                <select id="idPostazione" name="idPostazione" required>
                    <option value="">-- Seleziona postazione --</option>
                </select>
            </div>

            <!-- FASCIA ORARIA -->
            <div class="form-group">
                <label>Fascia oraria</label>
                <select name="idFascia" required>
                    <option value="1">08:00 - 12:00</option>
                    <option value="2">12:00 - 15:00</option>
                    <option value="3">15:00 - 18:00</option>
                </select>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn primary">
                    Conferma prenotazione
                </button>
                <a href="${pageContext.request.contextPath}/PrenotazioneController?action=list"
                   class="btn ghost">Annulla</a>
            </div>

        </form>
    </section>
</main>

<script>
    document.getElementById("ambiente").addEventListener("change", function () {

        const idAmbiente = this.value;
        const postazioni = document.getElementById("idPostazione");

        postazioni.innerHTML = '<option value="">-- Seleziona postazione --</option>';

        if (!idAmbiente) return;

        fetch("${pageContext.request.contextPath}/PrenotazioneController?action=postazioni&idAmbiente=" + idAmbiente)
            .then(r => r.text())
            .then(data => {
                data.trim().split("\n").forEach(riga => {
                    if (!riga) return;
                    const [id, numero] = riga.split(";");
                    const opt = document.createElement("option");
                    opt.value = id;
                    opt.textContent = "Postazione " + numero;
                    postazioni.appendChild(opt);
                });
            });
    });
</script>

</body>
</html>
