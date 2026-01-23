package it.unisa.oikonaos.dao;

import util.database; // Manteniamo la tua classe di connessione
import it.unisa.oikonaos.model.Ticket;

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

        String sql = """
        INSERT INTO ticket
        (Titolo, Descrizione, Categoria, Priorita, ID_Autore, Stato)
        VALUES (?, ?, ?, ?, ?, 'APERTO')
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps =
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

    // LISTA TICKET DI UN SINGOLO UTENTE
    public List<Ticket> doRetrieveByAutore(long idAutore) throws Exception {
        List<Ticket> lista = new ArrayList<>();
        String sql = "SELECT * FROM ticket WHERE ID_Autore = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
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

    // --- METODI PER IL SUPERVISORE (ADMIN) ---

    // 1. RECUPERA TUTTI I TICKET (Per la tabella generale)
    public List<Ticket> doRetrieveAll() throws Exception {
        List<Ticket> lista = new ArrayList<>();
        // Ordiniamo per data decrescente (i più recenti in alto)
        String sql = "SELECT * FROM ticket ORDER BY DataApertura DESC";
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

    // 2. RECUPERA SINGOLO TICKET PER ID (Per la pagina Dettagli - MANCAVA QUESTO)
    public Ticket doRetrieveById(long idTicket) throws Exception {
        String sql = "SELECT * FROM ticket WHERE ID_Ticket = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
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

    // 3. AGGIORNA STATO (Es. da Aperto a In Lavorazione)
    public void updateStato(long idTicket, String nuovoStato) throws Exception {
        String sql = "UPDATE ticket SET Stato = ? WHERE ID_Ticket = ?";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuovoStato);
            ps.setLong(2, idTicket);
            ps.executeUpdate();
        }
    }

    // CANCELLAZIONE (Solo se APERTO e dall'autore)
    public boolean deleteTicketIfAperto(long idTicket, long idAutore) throws Exception {
        String sql = "DELETE FROM ticket WHERE ID_Ticket = ? AND ID_Autore = ? AND Stato = 'APERTO'";
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, idTicket);
            ps.setLong(2, idAutore);
            return ps.executeUpdate() > 0;
        }
    }

}