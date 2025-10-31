<link rel="stylesheet" href="${pageContext.request.contextPath}/css/navbar.css" />

<header class="on-navbar" role="navigation" aria-label="Barra di navigazione principale">
    <!-- LOGO a sinistra -->
    <div class="on-left">
        <a href="${pageContext.request.contextPath}/HomeController" class="on-logo-link" aria-label="Vai alla home">
            <img class="on-logo" src="${pageContext.request.contextPath}/assets/oikonaosLogo.png" alt="Logo OikoNaos" />
        </a>
    </div>

    <!-- HAMBURGER + MENU (CSS-only) -->
    <div class="on-right">
        <!-- Checkbox invisibile per il toggle -->
        <input type="checkbox" id="onToggle" class="on-toggle" aria-hidden="true" />

        <!-- Etichetta cliccabile (le 3 linee) -->
        <label for="onToggle" class="on-hamb" aria-label="Apri menu" role="button">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="3" y1="6"  x2="21" y2="6"/>
                <line x1="3" y1="12" x2="21" y2="12"/>
                <line x1="3" y1="18" x2="21" y2="18"/>
            </svg>
        </label>

        <!-- Menu a comparsa -->
        <nav class="on-menu">
            <a href="${pageContext.request.contextPath}/HomeController">Home</a>
            <a href="${pageContext.request.contextPath}/LoginController">Login</a>
            <a href="${pageContext.request.contextPath}/RegisterController">Registrati</a>
            <a href="${pageContext.request.contextPath}/ProjectsController">Progetti</a>
            <a href="${pageContext.request.contextPath}/AssociazioneController">L&#39;associazione</a>
        </nav>
    </div>
</header>