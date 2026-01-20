<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <title>Area Supervisore - OikoNaos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .dashboard-container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 24px;
            margin-top: 40px;
        }

        .admin-card {
            text-align: center;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .admin-card:hover {
            transform: translateY(-10px);
        }

        .icon-box {
            font-size: 3rem;
            margin-bottom: 15px;
            display: block;
        }
    </style>
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="hero">
    <div class="hero-content">
        <h1 class="title">
            <span class="greek">Pannello di Amministrazione</span>
        </h1>

        <p class="tagline">Benvenuto nell’area di controllo. Gestisci i flussi e le segnalazioni della community.</p>

        <div class="dashboard-container">
            <a href="${pageContext.request.contextPath}/SupervisoreTicketController" class="btn ghost admin-card" style="padding: 40px 20px; height: auto; box-shadow: var(--shadow);">
                <span class="icon-box">🎫</span>
                <span style="font-size: 1.2rem; font-weight: 800; color: var(--ink);">Gestione Ticket</span>
                <p style="color: var(--muted); font-size: 0.9rem; margin-top: 8px;">Risolvi le segnalazioni degli utenti e chiudi i ticket aperti.</p>
            </a>

            <a href="${pageContext.request.contextPath}/SupervisorePrenotazioniController" class="btn ghost admin-card" style="padding: 40px 20px; height: auto; box-shadow: var(--shadow);">
                <span class="icon-box">📅</span>
                <span style="font-size: 1.2rem; font-weight: 800; color: var(--ink);">Gestione Prenotazioni</span>
                <p style="color: var(--muted); font-size: 0.9rem; margin-top: 8px;">Monitora l'occupazione delle sale e il riepilogo globale.</p>
            </a>

            <a href="${pageContext.request.contextPath}/SupervisoreEventiController" class="btn ghost admin-card" style="padding: 40px 20px; height: auto; box-shadow: var(--shadow);">
                <span class="icon-box">📌</span>
                <span style="font-size: 1.2rem; font-weight: 800; color: var(--ink);">Gestione Eventi</span>
                <p style="color: var(--muted); font-size: 0.9rem; margin-top: 8px;">Crea, modifica ed elimina gli eventi della community.</p>
            </a>

            <a href="${pageContext.request.contextPath}/SupervisoreTasseController"
               class="btn ghost admin-card"
               style="padding: 40px 20px; height: auto; box-shadow: var(--shadow);">
                <span class="icon-box">💰</span>
                <span style="font-size: 1.2rem; font-weight: 800; color: var(--ink);">
                        Gestione Tasse
                </span>
                <p style="color: var(--muted); font-size: 0.9rem; margin-top: 8px;">
                    Inserisci e consulta le tasse trimestrali della community.
                </p>
            </a>

        </div>
    </div>

    <div class="mascot">
        <div class="bubble">Hai tutto sotto controllo!</div>
    </div>
</main>

<footer class="footer">
    &copy; 2025 OikoNaos - Gestione Risorse
</footer>

</body>
</html>