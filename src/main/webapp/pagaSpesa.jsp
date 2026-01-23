<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="it.unisa.oikonaos.model.Pagamento" %>

<%
    Pagamento p = (Pagamento) request.getAttribute("pagamento");
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Pagamento spesa</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/main.css">
</head>

<body>

<jsp:include page="/include/header-navbar.jsp" />

<main class="dashboard">

    <h1 class="page-title">Pagamento spesa</h1>

    <section class="dashboard-card">

        <%
            if (p == null) {
        %>
        <p class="empty-state">
            Errore: pagamento non trovato o non valido.
        </p>

        <a href="${pageContext.request.contextPath}/SpeseController"
           class="btn primary">
            Torna alle spese
        </a>
        <%
                return;
            }
        %>

        <p><strong>Periodo:</strong> <%= p.getPeriodo() %></p>
        <p><strong>Importo:</strong> € <%= p.getImportoPagato() %></p>

        <form method="post"
              action="${pageContext.request.contextPath}/SpeseController">

            <input type="hidden" name="action" value="pay">
            <input type="hidden" name="idPagamento" value="<%= p.getIdPagamento() %>">

            <label class="section-title">Metodo di pagamento</label>

            <!-- SCELTA METODO -->
            <div class="form-group">
                <label>
                    <input type="radio" name="metodo" value="CARTA"
                           onclick="mostra('carta')">
                    Carta di credito / debito
                </label><br>

                <label>
                    <input type="radio" name="metodo" value="PAYPAL"
                           onclick="mostra('paypal')">
                    PayPal
                </label><br>

                <label>
                    <input type="radio" name="metodo" value="APPLEPAY"
                           onclick="mostra('apple')">
                    Apple Pay
                </label><br>

                <label>
                    <input type="radio" name="metodo" value="KLARNA"
                           onclick="mostra('klarna')">
                    Klarna
                </label>
            </div>

            <!-- CARTA -->
            <div id="cartaBox" class="form-group" style="display:none">

                <h3>Pagamento con carta</h3>

                <div class="form-row">
                    <div class="form-field">
                        <label>Numero carta</label>
                        <input type="text" name="numeroCarta" placeholder="1234 5678 9012 3456">
                    </div>

                    <div class="form-field">
                        <label>Nome titolare</label>
                        <input type="text" name="nomeCarta" placeholder="Mario Rossi">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-field">
                        <label>Data scadenza</label>
                        <input type="text" name="scadenza" placeholder="MM/AA">
                    </div>

                    <div class="form-field">
                        <label>CVV</label>
                        <input type="text" name="cvv" placeholder="123">
                    </div>
                </div>

            </div>

            <!-- PAYPAL -->
            <div id="paypalBox" class="form-group" style="display:none">
                <h3>Pagamento con PayPal</h3>

                <div class="payment-row">
                    <div>
                        <label>Email</label>
                        <input type="email">
                    </div>

                    <div>
                        <label>Password</label>
                        <input type="password">
                    </div>
                </div>
            </div>

            <div id="appleBox" class="form-group" style="display:none">
                <h3>Pagamento con Apple Pay</h3>

                <div class="payment-row">
                    <div>
                        <label>Apple ID</label>
                        <input type="email">
                    </div>

                    <div>
                        <label>Password</label>
                        <input type="password">
                    </div>
                </div>
            </div>

            <!-- KLARNA -->
            <div id="klarnaBox" class="form-group" style="display:none">
                <h3>Pagamento con Klarna</h3>

                <div class="payment-row">
                    <div>
                        <label>Email</label>
                        <input type="email">
                    </div>

                    <div>
                        <label>Codice di verifica</label>
                        <input type="text" placeholder="Codice OTP">
                    </div>
                </div>
            </div>

            <button type="submit" class="btn primary">
                Conferma pagamento
            </button>

            <a href="${pageContext.request.contextPath}/SpeseController"
               class="btn ghost">
                Annulla
            </a>
        </form>

    </section>
</main>

<script>
    function mostra(metodo) {

        const box = ["carta", "paypal", "apple", "klarna"];

        box.forEach(b => {
            const el = document.getElementById(b + "Box");
            if (el) el.style.display = "none";
        });

        const selected = document.getElementById(metodo + "Box");
        if (selected) selected.style.display = "block";
    }
</script>

</body>
</html>
