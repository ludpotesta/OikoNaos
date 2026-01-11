<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>OikoNaos - Risorse Condivise</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
</head>
<body>

<jsp:include page="/include/header-navbar.jsp" />

<%
    Utente u = (Utente) session.getAttribute("utente");
    String errore = request.getParameter("error");
    if ("disponibile".equals(errore)) {
%>
<script>
    alert("La risorsa richiesta non è disponibile");
</script>
<%
    }
%>

<main class="dashboard">

    <!-- GRID DELLE RISORSE PRENOTATE -->
    <section class="dashboard-grid">
        <h2>Risorse richieste</h2>

            <c:forEach var="r" items="${risorseRichieste}">
                <div class="dashboard-card active">
                    <h3>${r.nome}</h3>
                    <p>${r.descrizione}</p>
                    <span class="badge">${r.stato}</span>
                </div>
            </c:forEach>


    </section>

    <!-- GRID DI TUTTE LE RISORSE -->
    <section class="dashboard-grid">
        <h2>Risorse disponibili</h2>

            <c:forEach var="r" items="${risorseDisponibili}">
                <div class="dashboard-card active">
                    <h3>${r.nome}</h3>
                    <p>${r.descrizione}</p>

                    <form method="post" action="RisorsaController">
                        <input type="hidden" name="idRisorsa" value="${r.id}">
                        <button type="submit">Richiedi</button>
                    </form>
                </div>
            </c:forEach>


    </section>

</main>
</body>
</html>

