<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
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


%>

<main class="dashboard">

    <!-- HEADER CON MASCOTTE + SALUTO -->
    <section class="dashboard-header header-with-mascot">

        <!-- MASCOTTE -->
        <div class="header-mascot">
            <img
                    src="${pageContext.request.contextPath}/assets/ecateMascotte.png"
                    alt="Mascotte Ecate"
            />
        </div>

        <!-- TESTO -->
        <div class="header-text">
            <h1 class="dashboard-title">
                Ciao, <%= u.getNome() %>
            </h1>
            <p class="dashboard-subtitle">
                Da qui puoi cambiare i tuoi dati personali.
            </p>
        </div>

    </section>

    <!-- GRID DELLE FUNZIONALITÀ -->
    <section class="dashboard-grid">

        <form action="ProfiloController" method="post" class="dashboard-card">

            <input type="hidden" name="action" value="update">

            <label>Nome</label>
            <input type="text" name="nome" value="<%= u.getNome() %>"><br>

            <label>Cognome</label>
            <input type="text" name="cognome" value="<%= u.getCognome() %>"><br>

            <label>Email</label>
            <input type="email" name="email" value="<%= u.getEmail() %>"><br>

            <label>Numero di Telefono</label>
            <input type="tel" name="telefono" value="<%= u.getTelefono()%>"><br>

            <label>Nuovo username</label>
            <input type="text" name="newUsername"><br>

            <label>Nuova password</label>
            <input type="password" name="newPassword"><br>

            <label>Password attuale</label>
            <input type="password" name="currentPassword" required><br>

            <button type="submit">Salva modifiche</button>
        </form>

    </section>

</main>
<footer class="footer">
    &copy; 2025 OikoNaos - Area Coinquilino
</footer>

</body>
</html>
