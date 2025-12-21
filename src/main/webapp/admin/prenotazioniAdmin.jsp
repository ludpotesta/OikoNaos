<%@ page import="it.unisa.oikonaos.model.Prenotazione, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <title>Admin - Gestione Prenotazioni</title>
    </head>

    <body>
    <jsp:include page="/include/header-navbar.jsp" />

    <main style="padding: 20px; font-family: sans-serif;">

            <h1>Riepilogo Globale Prenotazioni (ADMIN)</h1>

            <%
                List<Prenotazione> lista =
                        (List<Prenotazione>) request.getAttribute("listaGlobalePrenotazioni");
            %>
            <% if (lista == null || lista.isEmpty()) { %>

            <p style="color: #555;">
                Nessuna prenotazione presente nel sistema.
            </p>
            <% } else { %>

            <table border="1" cellpadding="10" cellspacing="0">
                <tr style="background-color: #f0f0f0;">
                    <th>ID Prenotazione</th>
                    <th>Data</th>
                    <th>ID Utente</th>
                    <th>ID Postazione</th>
                </tr>

                <% for (Prenotazione p : lista) { %>
                <tr>
                    <td><%= p.getIdPrenotazione() %></td>
                    <td><%= p.getData() %></td>
                    <td><%= p.getIdUtente() %></td>
                    <td><%= p.getIdPostazione() %></td>
                </tr>
                <% } %>
            </table>
            <% } %>
        </main>
    </body>
</html>

