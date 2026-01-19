<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Utente" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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

        <c:if test="${empty richiesteAttive}">
            <p>Nessuna richiesta attiva.</p>
        </c:if>

            <c:forEach var="r" items="${richiesteAttive}">
                <div class="dashboard-card active">
                    <h3>${r.nomeRisorsa}</h3>
                    <p>
                        Dal ${r.dataInizio} al ${r.dataFine}
                    </p>
                    <strong>Stato: ${r.stato}</strong>
                </div>
            </c:forEach>


    </section>

    <!-- GRID DI TUTTE LE RISORSE -->
    <h2>Risorse Disponibili</h2>
    <c:if test="${empty risorseDisponibili}">
        <p>Nessuna risorsa disponibile.</p>
    </c:if>

    <c:forEach var="r" items="${risorseDisponibili}">
        <div class="dashboard-card active">
            <h3>${r.nome}</h3>
            <p>${r.descrizione}</p>

            <form action="RisorsaController" method="post">
                <input type="hidden" name="action" value="richiedi">
                <input type="hidden" name="idRisorsa" value="${r.idRisorsa}">

                <label>Dal:</label>
                <input type="datetime-local" name="dataInizio" required>

                <label>Al:</label>
                <input type="datetime-local" name="dataFine" required>

                <label>
                    <input type="checkbox" name="accettaRegole" required>
                    Accetto le regole
                </label>

                <button type="submit">Richiedi</button>
            </form>
        </div>
    </c:forEach>

</main>
</body>
</html>

