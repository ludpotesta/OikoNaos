<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Home</title>
    <%-- Includiamo il CSS per lo stile professionale --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<%
    // Recupero dell'utente per personalizzare il saluto
    Utente u = (Utente) session.getAttribute("utente");
%>

<main class="hero">
    <div class="hero-content">
        <%-- Titolo "Aesthetic" con font Bryndan Write sulla classe .word --%>
        <h1 class="title">
            Ciao, <span class="word"><%= (u != null) ? u.getNome() : "Ospite" %></span>
            <span class="greek">Area Coinquilino</span>
        </h1>
        <p class="tagline">Benvenuto nella tua area personale. Gestisci i tuoi spazi e le tue segnalazioni.</p>

        <%-- CTA: Pulsanti di navigazione pura --%>
        <div class="cta" style="margin-top: 30px; display: flex; gap: 15px; justify-content: center;">
            <a href="${pageContext.request.contextPath}/PrenotazioneController" class="btn primary">
                Le Mie Prenotazioni 📅
            </a>

            <%-- Collegamento corretto al Servlet TicketController --%>
            <a href="${pageContext.request.contextPath}/TicketController" class="btn ghost">
                I Miei Ticket 🎫
            </a>
        </div>
    </div>
</main>

<footer class="footer">
    &copy; 2025 OikoNaos - Community Space
</footer>

<%-- Inseriamo qui lo script corretto per evitare i pop-up involontari --%>
<script>
    // Selettore specifico: la modale apparirà SOLO per pulsanti con classe 'btn-annulla'
    // ignorando i normali pulsanti '.btn' della Home.
    document.querySelectorAll('.btn-annulla').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault(); // Impedisce l'azione immediata

            // Codice per mostrare la tua modale (es: modal.style.display = 'block')
            mostraModaleConferma();
        });
    });
</script>

</body>
</html>