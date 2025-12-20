<%@ page import="it.unisa.oikonaos.model.Prenotazione, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
    <head>
        <meta charset="UTF-8">
        <title>Le mie Prenotazioni</title>
    </head>

    <body>
        <jsp:include page="header.jsp" />
        <% if ("ok".equals(request.getParameter("msg"))) { %>
        <p style="color: green; font-weight: bold;">
            ✔ Prenotazione effettuata con successo
        </p>
        <% } %>

        <% if ("conflitto".equals(request.getParameter("error"))) { %>
        <p style="color: red; font-weight: bold;">
            ✖ Postazione già prenotata per quella fascia oraria
        </p>
        <% } %>

        <% if ("deleted".equals(request.getParameter("msg"))) { %>
        <p style="color: green; font-weight: bold;">
            ✔ Prenotazione annullata con successo
        </p>
        <% } %>

        <h1>Storico Prenotazioni</h1>

        <table border="1" cellpadding="10" cellspacing="0">
            <tr style="background-color:#f0f0f0;">
                <th>Data</th>
                <th>Postazione</th>
                <th>Fascia Oraria</th>
                <th>Azione</th>
            </tr>

            <%
                List<Prenotazione> lista =
                        (List<Prenotazione>) request.getAttribute("listaPrenotazioni");

                if (lista == null || lista.isEmpty()) {
            %>
            <tr>
                <td colspan="4" style="text-align:center; color:#555;">
                    Nessuna prenotazione presente.
                </td>
            </tr>
            <%
            } else {
                for (Prenotazione p : lista) {
            %>
            <tr>
                <td><%= p.getData() %></td>
                <td>Postazione <%= p.getIdPostazione() %></td>
                <td>Fascia <%= p.getIdFasciaOraria() %></td>
                <td>
                    <form action="PrenotazioneController" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="idPrenotazione"
                               value="<%= p.getIdPrenotazione() %>">
                        <button type="submit">Annulla</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                }
            %>
        </table>

        <br>
        <a href="nuovaPrenotazione.jsp"> Effettua una nuova prenotazione</a>

    </body>
</html>

