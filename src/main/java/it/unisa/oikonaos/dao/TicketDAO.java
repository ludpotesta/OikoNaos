package it.unisa.oikonaos.dao;

// ATTENZIONE: Se questa riga sotto diventa rossa, cambiala in "import util.database;"

import it.unisa.oikonaos.model.Ticket;
import util.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    /* CREAZIONE TICKET */
    public long creaTicket(String titolo,
                           String descrizione,
                           String categoria,
                           String priorita,
                           long idAutore) throws Exception {

        String sql = "INSERT INTO ticket (Titolo, Descrizione, Categoria, Priorita, ID_Autore, Stato) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titolo);
            ps.setString(2, descrizione);
            ps.setString(3, categoria);
            ps.setString(4, priorita);
            ps.setLong(5, idAutore);
            ps.setString(6, "APERTO"); // Impostiamo lo stato esplicitamente

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        }
    }

    // LISTA TICKET DI UN SINGOLO UTENTE
    public List<Ticket> doRetrieveByAutore(long idAutore) throws Exception {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE ID_Autore = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idAutore);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ticket t = new Ticket();
                    t.setIdTicket(rs.getLong("ID_Ticket"));
                    t.setTitolo(rs.getString("Titolo"));
                    t.setDescrizione(rs.getString("Descrizione"));
                    t.setCategoria(rs.getString("Categoria"));
                    t.setPriorita(rs.getString("Priorita"));
                    t.setStato(rs.getString("Stato"));
                    t.setDataApertura(rs.getTimestamp("DataApertura"));
                    t.setIdAutore(rs.getLong("ID_Autore"));
                    lista.add(t);
                }
            }
        }
        return lista;
    }

    // --- METODI PER IL SUPERVISORE (ADMIN) ---

    // 1. RECUPERA TUTTI I TICKET (Per la tabella generale)
    public List<Ticket> doRetrieveAll() throws Exception {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM Ticket ORDER BY DataApertura DESC";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
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

    // 2. RECUPERA SINGOLO TICKET PER ID
    public Ticket doRetrieveById(long idTicket) throws Exception {
        String sql = "SELECT * FROM Ticket WHERE ID_Ticket = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTicket);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        }
        return null;
    }

    // 3. AGGIORNA STATO
    public void updateStato(long idTicket, String nuovoStato) throws Exception {
        String sql = "UPDATE Ticket SET Stato = ? WHERE ID_Ticket = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovoStato);
            ps.setLong(2, idTicket);
            ps.executeUpdate();
        }
    }

    // 4. RICERCA FILTRATA (IL METODO CHE MANCAVA)
    public List<Ticket> doRetrieveFiltered(String stato, String categoria, String priorita) throws Exception {
        List<Ticket> lista = new ArrayList<>();

        // Iniziamo con una query base
        StringBuilder sql = new StringBuilder("SELECT * FROM Ticket WHERE 1=1");

        // Aggiungiamo i filtri solo se sono stati selezionati
        if (stato != null && !stato.isEmpty()) {
            sql.append(" AND Stato = ?");
        }
        if (categoria != null && !categoria.isEmpty()) {
            sql.append(" AND Categoria = ?");
        }
        if (priorita != null && !priorita.isEmpty()) {
            sql.append(" AND Priorita = ?");
        }

        sql.append(" ORDER BY DataApertura DESC");

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            // Riempiamo i punti interrogativi dinamicamente
            int index = 1;
            if (stato != null && !stato.isEmpty()) {
                ps.setString(index++, stato);
            }
            if (categoria != null && !categoria.isEmpty()) {
                ps.setString(index++, categoria);
            }
            if (priorita != null && !priorita.isEmpty()) {
                ps.setString(index++, priorita);
            }

            try (ResultSet rs = ps.executeQuery()) {
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
        }
        return lista;
    }

    // CANCELLAZIONE
    public boolean deleteTicketIfAperto(long idTicket, long idAutore) throws Exception {
        String sql = "DELETE FROM Ticket WHERE ID_Ticket = ? AND ID_Autore = ? AND Stato = 'APERTO'";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTicket);
            ps.setLong(2, idAutore);
            return ps.executeUpdate() > 0;
        }
    }
}