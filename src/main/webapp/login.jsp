<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="it">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>OikoNaos - Login</title>

    <link rel="icon" href="${pageContext.request.contextPath}/assets/favicon.svg">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;800&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css" />
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />
<%
    String errore = request.getParameter("error");
    String from = request.getParameter("from");

    if ("login".equals(errore)) {
%>
<script>
    alert("Accesso negato: non hai i permessi per visualizzare questa pagina.");
</script>
<%
} else if ("session".equals(errore)) {
%>
<script>
    alert("Accesso negato: devi effettuare il login.");
</script>
<%
    }
%>

<main class="login-page">

    <div class="login-wrapper">

        <img src="${pageContext.request.contextPath}/assets/oikonaosLogo.png"
             alt="OikoNaos"
             class="login-logo">

        <section class="login-card">

            <h2>Accedi a OikoNaos</h2>

            <% if ("credenziali".equals(request.getParameter("error"))) { %>
            <div class="login-error">
                Username o password errati.
            </div>
            <%
            } else if ("seesion".equals(request.getParameter("error"))) {
            %>
            <div class="login-error">
                Non sei loggato, accedi per continuare.
            </div>
            <%
                } if ("generico".equals(request.getParameter("error"))) {
            %>
            <div class="login-error">
                Errore di sistema. Riprova più tardi.
            </div>
            <% } %>


            <form action="${pageContext.request.contextPath}/AutenticazioneController"
                  method="post">

                <input type="text"
                       name="username"
                       placeholder="Username"
                       required>

                <div class="password-wrapper">
                    <input
                            id="password"
                            type="password"
                            name="password"
                            placeholder="Password"
                            required
                    />

                    <button
                            type="button"
                            class="toggle-password"
                            id="togglePassword"
                            aria-label="Mostra password"
                    >
                        <svg class="icon-eye" viewBox="0 0 24 24">
                            <path d="M2.3 12s3.6-7 9.7-7 9.7 7 9.7 7-3.6 7-9.7 7S2.3 12 2.3 12Z"
                                  fill="none" stroke="currentColor" stroke-width="2"/>
                            <circle cx="12" cy="12" r="3"
                                    fill="none" stroke="currentColor" stroke-width="2"/>
                        </svg>

                        <svg class="icon-eye-off" viewBox="0 0 24 24">
                            <line x1="3" y1="3" x2="21" y2="21"
                                  stroke="currentColor" stroke-width="2"/>
                        </svg>
                    </button>
                </div>

                <button type="submit">
                    Accedi
                </button>

                <p style="margin-top: 16px; text-align: center;">
                    <a href="${pageContext.request.contextPath}/recupera-password.jsp"
                       style="color: #dbe5f0; font-size: 0.9rem;">
                        Password dimenticata?
                    </a>
                </p>

            </form>

        </section>

    </div>

</main>

<footer class="footer">
    <small>© 2025 OikoNaos — Co-housing, insieme.</small>
</footer>

<script>
    (function () {
        const pwd = document.getElementById("password");
        const btn = document.getElementById("togglePassword");
        if (!pwd || !btn) return;

        function updateButtonVisibility() {
            const hasValue = pwd.value.length > 0;
            btn.classList.toggle("visible", hasValue);
            if (!hasValue) {
                pwd.type = "password";
                btn.classList.remove("active");
            }
        }

        pwd.addEventListener("input", updateButtonVisibility);

        btn.addEventListener("click", () => {
            const isVisible = pwd.type === "text";
            pwd.type = isVisible ? "password" : "text";
            btn.classList.toggle("active", !isVisible);
            pwd.focus();
        });

        updateButtonVisibility();
    })();
</script>
</body>
</html>
