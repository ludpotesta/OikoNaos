<%@ page import="it.unisa.oikonaos.model.*, java.util.*" %>
<%
    Utente utente = (Utente) session.getAttribute("utente");
    List<Prenotazione> lista = (List<Prenotazione>) request.getAttribute("listaPrenotazioni");
%>
<html>
<body>
<h1>Le tue prenotazioni, <%= utente.getNome() %></h1>
<table border="1">
    <tr>
        <th>Data</th><th>Postazione</th><th>Fascia</th><th>Azione</th>
    </tr>
    <% if(lista != null) {
        for(Prenotazione p : lista) { %>
    <tr>
        <td><%= p.getData() %></td>
        <td><%= p.getIdPostazione() %></td>
        <td><%= p.getIdFasciaOraria() %></td>
        <td>
            <form action="PrenotazioneControl" method="post">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="idPrenotazione" value="<%= p.getIdPrenotazione() %>">
                <button type="submit">Annulla</button>
            </form>
        </td>
    </tr>
    <% } } %>
</table>
<br>
<a href="nuovaPrenotazione.jsp">Effettua una nuova prenotazione</a>
</body>
</html>
