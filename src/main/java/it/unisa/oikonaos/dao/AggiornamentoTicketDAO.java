package it.unisa.oikonaos.dao;

import util.database;
import java.sql.*;
import it.unisa.oikonaos.model.AggiornamentoTicket;
import java.util.ArrayList;
import java.util.List;

public class AggiornamentoTicketDAO {

    public void creaAggiornamento(long idTicket, long idAutore, String msg) throws Exception {

        String sql = """
            INSERT INTO aggiornamentoticket (Messaggio, ID_Ticket, ID_Autore)
            VALUES (?, ?, ?)
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, msg);
            ps.setLong(2, idTicket);
            ps.setLong(3, idAutore);

            ps.executeUpdate();
        }
    }

    public List<AggiornamentoTicket> doRetrieveByTicket(long idTicket) throws Exception {

        List<AggiornamentoTicket> lista = new ArrayList<>();

        String sql = """
            SELECT a.Messaggio,
                   a.DataAggiornamento,
                   u.Nome,
                   u.Cognome
            FROM aggiornamentoticket a
            JOIN utente u ON a.ID_Autore = u.ID_Utente
            WHERE a.ID_Ticket = ?
            ORDER BY a.DataAggiornamento DESC
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTicket);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AggiornamentoTicket a = new AggiornamentoTicket();
                a.setMessaggio(rs.getString("Messaggio"));
                a.setDataAggiornamento(rs.getTimestamp("DataAggiornamento"));
                a.setNomeUtente(rs.getString("Nome"));
                a.setCognomeUtente(rs.getString("Cognome"));

                lista.add(a);
            }
        }

        return lista;
    }
}
