<%@ page import="it.unisa.oikonaos.model.Prenotazione, java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Prenotazioni - OikoNaos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        /* Integrazione stili specifici per la tabella in linea con il tuo main.css */
        .table-container {
            background: var(--card);
            border-radius: 18px;
            box-shadow: var(--shadow);
            padding: 24px;
            margin-top: 30px;
            overflow-x: auto;
            border: 1px solid rgba(0,0,0,0.05);
        }

        .aesthetic-table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        .aesthetic-table th {
            padding: 16px;
            color: var(--muted);
            font-weight: 700;
            text-transform: uppercase;
            font-size: 12px;
            letter-spacing: 1px;
            border-bottom: 2px solid var(--bg);
        }

        .aesthetic-table td {
            padding: 18px 16px;
            border-bottom: 1px solid var(--bg);
            color: var(--ink);
            font-size: 15px;
        </tr>

        .aesthetic-table tr:hover {
            background-color: #f2fbfb; /* Richiamo del colore brand molto tenue */
        }

            .id-badge {
                color: var(--brand);
                font-weight: 800;
            }

            .status-pill {
                background: var(--bg);
                color: var(--brand-ink);
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 700;
                display: inline-block;
            }

            .ambiente-text {
                font-family: 'Bryndan Write', cursive;
                font-size: 1.2rem;
                color: var(--ink);
            }
    </style>
</head>

<body>
<jsp:include page="/include/header-navbar.jsp" />

<main class="hero" style="display: block; padding-top: 40px;">
    <div class="hero-content">
        <h1 class="title">
            Gestione <span class="word">Prenotazioni</span>
            <span class="greek">Riepilogo Globale</span>
        </h1>
        <p class="tagline">Monitora l'occupazione delle postazioni e lo stato degli ambienti.</p>

        <%
            List<Prenotazione> lista = (List<Prenotazione>) request.getAttribute("listaGlobalePrenotazioni");
        %>

        <% if (lista == null || lista.isEmpty()) { %>
        <div class="alert" style="justify-content: center; margin-top: 40px;">
            Nessuna prenotazione presente nel sistema.
        </div>
        <% } else { %>

        <div class="table-container">
            <table class="aesthetic-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Data</th>
                    <th>Stato</th>
                    <th>Utente</th>
                    <th>Postazione</th>
                    <th>Ambiente</th>
                </tr>
                </thead>
                <tbody>
                <% for (Prenotazione p : lista) { %>
                <tr>
                    <td class="id-badge">#<%= p.getIdPrenotazione() %></td>
                    <td><b><%= p.getData() %></b></td>
                    <td>
                        <span class="status-pill"><%= p.getStato() %></span>
                    </td>
                    <td>ID: <%= p.getIdUtente() %></td>
                    <td>Postazione <%= p.getIdPostazione() %></td>
                    <td class="ambiente-text">
                        <%= (p.getNomeAmbiente() != null) ? p.getNomeAmbiente() : "N/D" %>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
        <% } %>
    </div>
</main>

<footer class="footer">
    &copy; 2025 OikoNaos - Gestione Amministrativa
</footer>
</body>
</html>