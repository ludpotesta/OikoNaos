<%@ page import="it.unisa.oikonaos.model.Utente" %>
<%
    Utente utente = (Utente) session.getAttribute("utente");
    String ruolo = (utente != null && utente.getRuolo() != null) ? utente.getRuolo() : "";
%>
<nav style="background: #e3f2fd; padding: 15px; border-bottom: 2px solid #1976d2; font-family: sans-serif;">
    <a href="home.jsp" style="text-decoration: none; font-weight: bold; color: #1976d2;">🏠 OikoNaos</a> |

    <% if (utente == null) { %>
    <a href="login.jsp">Login</a>
    <% } else { %>
    <a href="PrenotazioneController">Le mie Prenotazioni</a> |
    <a href="TicketController">I miei Ticket</a> |

    <% if (ruolo.equalsIgnoreCase("ADMIN")) { %>
    <span style="background: #ffcdd2; padding: 5px; border-radius: 4px;">
                <strong>Area Admin:</strong>
                <a href="AdminPrenotazioniController" style="color: #d32f2f;">Tutte Prenotazioni</a> |
                <a href="AdminTicketController" style="color: #d32f2f;">Tutti Ticket</a>
            </span> |
    <% } %>

    <span>Ciao, <strong><%= utente.getNome() %></strong></span> |
    <a href="LogoutController" style="color: #555;">Logout</a>
    <% } %>
</nav>
<br>
