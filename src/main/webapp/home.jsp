<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>

<%
    Utente u = (Utente) session.getAttribute("utente");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <title>OikoNaos - Home</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    </head>
    <body>

        <jsp:include page="/include/header-navbar.jsp" />

        <main class="container">
            <h1>Ciao, <%= u.getNome() %> 👋</h1>
            <p>Benvenuto in OikoNaos. Scegli cosa vuoi fare.</p>

            <%
                String errore = request.getParameter("error");
                if ("ruolo".equals(errore)) {
            %>
            <p class="alert">Non disponi delle autorizzazioni necessarie per accedere.</p>
            <%
                }
            %>

            <div class="grid-cards">
                <a class="card" href="${pageContext.request.contextPath}/profilo.jsp">
                    <h3>Profilo</h3>
                    <p>Visualizza e modifica i tuoi dati.</p>
                </a>

                <a class="card" href="${pageContext.request.contextPath}/PrenotazioneController">
                    <h3>Prenotazioni</h3>
                    <p>Gestisci le tue prenotazioni degli spazi.</p>
                </a>

                <a class="card" href="${pageContext.request.contextPath}/TicketController">
                    <h3>Ticket</h3>
                    <p>Apri e monitora segnalazioni di manutenzione.</p>
                </a>

                <a class="card danger" href="${pageContext.request.contextPath}/LogoutController">
                    <h3>Logout</h3>
                    <p>Esci dal tuo account.</p>
                </a>

                <% if ("SUPERVISORE".equalsIgnoreCase(u.getRuolo())) { %>
                <a class="card" href="${pageContext.request.contextPath}/admin/ticketAdmin.jsp">
                    <h3>Area Supervisore - Ticket</h3>
                    <p>Gestisci i ticket della comunità.</p>
                </a>
                <a class="card" href="${pageContext.request.contextPath}/AdminPrenotazioneController">
                    <h3>Area Supervisore - Prenotazioni</h3>
                    <p>Gestisci le prenotazioni della comunità.</p>
                </a>
                <% } %>
            </div>
        </main>

    </body>
</html>
