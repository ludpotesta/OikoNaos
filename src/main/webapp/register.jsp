<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos - Registrazione</title>

    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="login-page">

    <div class="login-wrapper">

        <img src="${pageContext.request.contextPath}/assets/oikonaosLogo.png"
             alt="OikoNaos"
             class="login-logo">

        <section class="login-card">

            <h2>Registrati a OikoNaos</h2>

            <%-- Errori --%>
            <%
                String error = request.getParameter("error");
                if ("email".equals(error)) {
            %>
            <div class="login-error">
                Questa email è già registrata.
            </div>
            <%
            } else if ("codice".equals(error)) {
            %>
            <div class="login-error">
                Codice identificativo non valido.
            </div>

            <%
            } else if ("username".equals(error)) {
            %>
            <div class="login-error">
                L'username inserito è già stato usato, inserisci un nuovo username.
            </div>
            <%
                } else if ("generico".equals(error)) {
            %>
            <div class="login-error">
                Errore durante la registrazione.
            </div>
            <%
                }
            %>


            <form action="${pageContext.request.contextPath}/RegistrazioneController"
                  method="post">

                <input type="text" name="nome" placeholder="Nome" required>
                <input type="text" name="cognome" placeholder="Cognome" required>
                <input type="email" name="email" placeholder="Email" required>
                <input type="tel" name="telefono" placeholder="Numero di telefono" required>
                <input type="text" name="username" placeholder="Username" required>
                <input type="password" name="password" placeholder="Password" required>
                <input type="text" name="codiceID" placeholder="Codice identificativo" required>

                <button type="submit">
                    Registrati
                </button>

            </form>

        </section>

    </div>

</main>

<footer class="footer">
    <small>© 2025 OikoNaos — Co-housing, insieme.</small>
</footer>

</body>
</html>
