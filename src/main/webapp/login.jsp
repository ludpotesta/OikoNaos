<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos - Login</title>

    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&family=Playfair+Display:wght@700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>
<body>
    <%@ include file="/include/header-navbar.jsp" %>

    <main class="hero">
        <form action="${pageContext.request.contextPath}/AutenticazioneController" method="post" class="form-section">
            <h2>Accedi a OikoNaos</h2>
            <div>
                <label>Username:</label>
                <input type="text" name="username" required class="form-input">
            </div>
            <div>
                <label>Password:</label>
                <input type="password" name="password" required class="form-input">
            </div>
            <input type="submit" value="Login" class="form-submit">
        </form>

    </main>

    <footer class="footer">
        <small>© 2025 OikoNaos — Co-housing, insieme.</small>
    </footer>
</body>
</html>
