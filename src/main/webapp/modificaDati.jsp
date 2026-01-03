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

        <form action="ModificaDatiController" method="post" class="dashboard-card">

            <input type="hidden" name="action" value="update">

            <label>Nome</label>
            <input type="text" name="nome" value="<%= u.getNome() %>"><br>

            <label>Cognome</label>
            <input type="text" name="cognome" value="<%= u.getCognome() %>"><br>

            <label>Email</label>
            <input type="email" name="email" value="<%= u.getEmail() %>"><br>

            <label>Numero di Telefono</label>
            <input type="tel" name="telefono" value="<%= u.getTelefono()%>"><br>

            <button type="submit">Salva modifiche</button>
        </form>

        <form action="ModificaCredenzialiController" method="post" class="dashboard-card">

            <label>Username attuale: <%= username %></label><br>
            <label>Nuovo username:</label>
            <input type="text" name="nuovoUsername"><br>

            <hr>

            <label>Password attuale</label>
            <input type="password" name="passwordAttuale"><br>

            <label>Nuova password</label>
            <input type="password" name="nuovaPassword"><br>

            <label>Conferma nuova password</label>
            <input type="password" name="confermaPassword"><br>

            <button type="submit">Aggiorna credenziali</button>
        </form>


    </section>

</main>
<footer class="footer">
    &copy; 2025 OikoNaos - Area Coinquilino
</footer>

</body>
</html>
