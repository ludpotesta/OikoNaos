package it.unisa.oikonaos.dao;

import util.database; // Manteniamo la tua classe di connessione
import it.unisa.oikonaos.model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public long creaTicket(String titolo, String descrizione, String categoria, String priorita, long idAutore) throws Exception {
        try (Connection con = database.getConnection()) {
            return creaTicket(con, titolo, descrizione, categoria, priorita, idAutore);
        }
    }
    public long creaTicket(Connection con, String titolo, String descrizione, String categoria, String priorita, long idAutore) throws Exception {

        String sql = """
        INSERT INTO ticket
        (Titolo, Descrizione, Categoria, Priorita, ID_Autore, Stato)
        VALUES (?, ?, ?, ?, ?, 'APERTO')
    """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titolo);
            ps.setString(2, descrizione);
            ps.setString(3, categoria);
            ps.setString(4, priorita);
            ps.setLong(5, idAutore);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
            return 0;
        }
    }

    public List<Ticket> doRetrieveByAutore(long idAutore) throws Exception {
        try (Connection con = database.getConnection()) {
            return doRetrieveByAutore(con, idAutore);
        }
    }
    public List<Ticket> doRetrieveByAutore(Connection con, long idAutore) throws Exception {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM ticket WHERE ID_Autore = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idAutore);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ticket t = new Ticket();
                t.setIdTicket(rs.getLong("ID_Ticket"));
                t.setTitolo(rs.getString("Titolo"));
                t.setDescrizione(rs.getString("Descrizione"));
                t.setCategoria(rs.getString("Categoria"));
                t.setPriorita(rs.getString("Priorita"));
                t.setStato(rs.getString("Stato"));
                t.setDataApertura(rs.getTimestamp("DataApertura"));
                lista.add(t);
            }
        }
        return lista;
    }

    public List<Ticket> doRetrieveAll() throws Exception {
        try (Connection con = database.getConnection()) {
            return doRetrieveAll(con);
        }
    }
    public List<Ticket> doRetrieveAll(Connection con) throws Exception {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM ticket ORDER BY DataApertura DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Ticket t = new Ticket();
                t.setIdTicket(rs.getLong("ID_Ticket"));
                t.setTitolo(rs.getString("Titolo"));
                t.setDescrizione(rs.getString("Descrizione"));
                t.setCategoria(rs.getString("Categoria"));
                t.setPriorita(rs.getString("Priorita"));
                t.setStato(rs.getString("Stato"));
                t.setIdAutore(rs.getLong("ID_Autore"));
                t.setDataApertura(rs.getTimestamp("DataApertura"));
                lista.add(t);
            }
        }
        return lista;
    }

    public Ticket doRetrieveById(long idTicket) throws Exception {
        try (Connection con = database.getConnection()) {
            return doRetrieveById(con, idTicket);
        }
    }
    public Ticket doRetrieveById(Connection con, long idTicket) throws Exception {
        String sql = "SELECT * FROM ticket WHERE ID_Ticket = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idTicket);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Ticket t = new Ticket();
                t.setIdTicket(rs.getLong("ID_Ticket"));
                t.setTitolo(rs.getString("Titolo"));
                t.setDescrizione(rs.getString("Descrizione"));
                t.setCategoria(rs.getString("Categoria"));
                t.setPriorita(rs.getString("Priorita"));
                t.setStato(rs.getString("Stato"));
                t.setIdAutore(rs.getLong("ID_Autore"));
                t.setDataApertura(rs.getTimestamp("DataApertura"));
                return t;
            }
            return null; // Nessun ticket trovato
        }
    }

    public void updateStato(long idTicket, String nuovoStato) throws Exception {
        try (Connection con = database.getConnection()) {
            updateStato(con, idTicket, nuovoStato);
        }
    }
    public void updateStato(Connection con, long idTicket, String nuovoStato) throws Exception {
        String sql = "UPDATE ticket SET Stato = ? WHERE ID_Ticket = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuovoStato);
            ps.setLong(2, idTicket);
            ps.executeUpdate();
        }
    }

    // CANCELLAZIONE (Solo se APERTO e dall'autore)
    public boolean deleteTicketIfAperto(long idTicket, long idAutore) throws Exception {
        try (Connection con = database.getConnection()) {
            return deleteTicketIfAperto(con, idTicket, idAutore);
        }
    }
    public boolean deleteTicketIfAperto(Connection con, long idTicket, long idAutore) throws Exception {
        String sql = "DELETE FROM ticket WHERE ID_Ticket = ? AND ID_Autore = ? AND Stato = 'APERTO'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idTicket);
            ps.setLong(2, idAutore);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Ticket> doRetrieveFiltered(String stato, String priorita, String data) throws Exception {
        try (Connection con = database.getConnection()) {
            return doRetrieveFiltered(con, stato, priorita, data);
        }
    }
    public List<Ticket> doRetrieveFiltered(Connection con, String stato, String priorita, String data)
            throws Exception {

        StringBuilder sql = new StringBuilder("SELECT * FROM ticket WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (stato != null && !stato.isBlank()) {
            sql.append(" AND Stato = ?");
            params.add(stato);
        }

        if (priorita != null && !priorita.isBlank()) {
            sql.append(" AND Priorita = ?");
            params.add(priorita);
        }

        if (data != null && !data.isBlank()) {
            sql.append(" AND DATE(DataApertura) = ?");
            params.add(Date.valueOf(data));
        }

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<Ticket> lista = new ArrayList<>();

            while (rs.next()) {
                Ticket t = new Ticket();
                t.setIdTicket(rs.getLong("ID_Ticket"));
                t.setTitolo(rs.getString("Titolo"));
                t.setStato(rs.getString("Stato"));
                t.setPriorita(rs.getString("Priorita"));
                lista.add(t);
            }

            return lista;
        }
    }
}