<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Il mio profilo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<%
    Utente u = (Utente) session.getAttribute("utente");
%>

<main class="dashboard">

    <h1 class="page-title">Il mio profilo</h1>

    <section class="dashboard-card">

        <h2 class="section-title">Dati personali</h2>

        <form action="${pageContext.request.contextPath}/ModificaDatiController"
              method="post">

            <div class="form-group">
                <label>Nome</label>
                <input type="text"
                       name="nome"
                       class="form-input"
                       value="<%= u.getNome() %>"
                       required>
            </div>

            <div class="form-group">
                <label>Cognome</label>
                <input type="text"
                       name="cognome"
                       class="form-input"
                       value="<%= u.getCognome() %>"
                       required>
            </div>

            <div class="form-group">
                <label>Email</label>
                <input type="email"
                       name="email"
                       class="form-input"
                       value="<%= u.getEmail() %>"
                       required>
            </div>

            <div class="form-group">
                <label>Telefono</label>
                <input type="tel"
                       name="telefono"
                       class="form-input"
                       value="<%= u.getTelefono() %>">
            </div>

            <button type="submit" class="btn primary">
                Salva dati personali
            </button>
        </form>

        <hr class="divider">


        <h2 class="section-title">Credenziali di accesso</h2>

        <form action="${pageContext.request.contextPath}/ModificaCredenzialiController"
              method="post">

            <div class="form-group">
                <label>Nuovo username (opzionale)</label>
                <input type="text"
                       name="nuovoUsername"
                       class="form-input"
                       placeholder="Lascia vuoto per non modificarlo">
            </div>

            <div class="form-group">
                <label>Password attuale *</label>
                <input type="password"
                       name="passwordAttuale"
                       class="form-input"
                       required>
            </div>

            <div class="form-group">
                <label>Nuova password (opzionale)</label>
                <input type="password"
                       name="nuovaPassword"
                       class="form-input">
            </div>

            <div class="form-group">
                <label>Conferma nuova password</label>
                <input type="password"
                       name="confermaPassword"
                       class="form-input">
            </div>

            <button type="submit" class="btn primary">
                Aggiorna credenziali
            </button>
        </form>

    </section>

</main>

<footer class="footer">
    &copy; 2025 OikoNaos
</footer>

</body>
</html>
