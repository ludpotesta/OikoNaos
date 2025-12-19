<%@ page import="it.unisa.oikonaos.model.Utente" %>
<% Utente utente = (Utente) request.getSession().getAttribute("utente"); %>
<html>
<body>
<h1>Le tue prenotazioni, <%= utente.getCognome() %></h1>
<p>Lista in fase di caricamento...</p>
<a href="nuovaPrenotazione.jsp">Fai una nuova prenotazione</a>
</body>
</html>
