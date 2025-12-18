<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos - Registrazione</title>

    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&family=Playfair+Display:wght@700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>
<%@ include file="/include/header-navbar.jsp" %>

<main class="hero">
    <form action="${pageContext.request.contextPath}/RegisterServlet" method="post" class="form-section">
        <h2>Registrati a OikoNaos</h2>
        <div><label>Nome:</label><input type="text" name="nome" required class="form-input"></div>
        <div><label>Cognome:</label><input type="text" name="cognome" required class="form-input"></div>
        <div><label>Email:</label><input type="text" name="email" required class="form-input"></div>
        <div><label>Numero di telefono:</label><input type="tel" name="telefono" required class="form-input"></div>
        <div><label>Username:</label><input type="text" name="username" required class="form-input"></div>
        Immetti una password e il codice ricevuto alla
        stipulazione del contratto
        <div><br><label>Password:</label><input type="password" name="password" required class="form-input"></div>
        <div><label>Codice:</label><input type="text" name="codiceID" required class="form-input"></div>
        <input type="submit" value="Login" class="form-submit">
    </form>
</main>

<footer class="footer">
    <small>© 2025 OikoNaos — Co-housing, insieme.</small>
</footer>
</body>
</html>
