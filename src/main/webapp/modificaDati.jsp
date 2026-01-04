<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<%@ page import="it.unisa.oikonaos.dao.CredenzialiDAO" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Modifica i tuoi dati</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<%

    Utente u = (Utente) session.getAttribute("utente");
    CredenzialiDAO credDAO = new CredenzialiDAO();
    String username = credDAO.getUsernameByUtente(u.getIdUtente());

%>

<main class="dashboard">

    <h1 class="page-title">Il mio Profilo</h1>

    <section class="dashboard-card">

        <!-- SEZIONE DATI PERSONALI -->
        <h2 class="section-title">Dati personali</h2>

        <form action="ModificaDatiController" method="post">

            <input type="hidden" name="action" value="update">

            <div class="form-group">
                <label>Nome</label>
                <input type="text" class="form-input" name="nome" value="<%= u.getNome() %>">
            </div>

            <div class="form-group">
                <label>Cognome</label>
                <input type="text" class="form-input" name="cognome" value="<%= u.getCognome() %>">
            </div>

            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" class="form-input"
                       value="<%= u.getEmail() %>">
            </div>

            <div class="form-group">
                <label>Numero di telefono</label>
                <input type="tel" name="telefono" class="form-input"
                       value="<%= u.getTelefono() %>">
            </div>

            <button type="submit" class="btn primary">
                Salva modifiche
            </button>
        </form>

        <hr>

        <!-- SEZIONE CREDENZIALI -->
        <h2 class="section-title">Credenziali di accesso</h2>

        <p class="info-text">
            Username attuale: <strong><%= username %></strong>
        </p>

        <form action="ModificaCredenzialiController" method="post">

            <div class="form-group">
                <label>Nuovo username</label>
                <input type="text" name="nuovoUsername" class="form-input">
            </div>

            <div class="form-group">
                <label>Password attuale</label>
                <input type="password" name="passwordAttuale" class="form-input">
            </div>

            <div class="form-group">
                <label>Nuova password</label>
                <input type="password" name="nuovaPassword" class="form-input">
            </div>

            <div class="form-group">
                <label>Conferma nuova password</label>
                <input type="password" name="confermaPassword" class="form-input">
            </div>

            <button type="submit" class="btn primary">
                Aggiorna credenziali
            </button>
        </form>

    </section>

</main>
<footer class="footer">
    &copy; 2025 OikoNaos - Area Coinquilino
</footer>

</body>
</html>
