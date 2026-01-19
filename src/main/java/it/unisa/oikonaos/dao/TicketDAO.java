package it.unisa.oikonaos.dao;

import util.database;
import it.unisa.oikonaos.model.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO per la gestione dei ticket di assistenza.
 * Contiene esclusivamente logica di accesso ai dati (CRUD),
 * in accordo con il pattern MVC.
 */
public class TicketDAO {

    /* ==========================================================
       CREAZIONE TICKET
       ========================================================== */

    public long creaTicket(String titolo,
                           String descrizione,
                           String categoria,
                           String priorita,
                           long idAutore) throws Exception {

        String sql = """
            INSERT INTO Ticket
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

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    /* ==========================================================
       RETRIEVE TICKET PER AUTORE (COINQUILINO)
       ========================================================== */

    public List<Ticket> doRetrieveByAutore(long idAutore)
            throws Exception {

        List<Ticket> lista = new ArrayList<>();

        String sql = """
            SELECT
                ID_Ticket,
                Titolo,
                Descrizione,
                Categoria,
                Priorita,
                Stato,
                DataApertura
            FROM Ticket
            WHERE ID_Autore = ?
            ORDER BY DataApertura DESC
        """;

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

                    lista.add(t);
                }
            }
        }
        return lista;
    }

    /* ==========================================================
       RETRIEVE TUTTI I TICKET (SUPERVISORE)
       ========================================================== */

    public List<Ticket> doRetrieveAll()
            throws Exception {

        List<Ticket> lista = new ArrayList<>();

        String sql = """
            SELECT
                ID_Ticket,
                Titolo,
                Descrizione,
                Stato,
                ID_Autore,
                DataApertura
            FROM Ticket
            ORDER BY DataApertura DESC
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ticket t = new Ticket();
                t.setIdTicket(rs.getLong("ID_Ticket"));
                t.setTitolo(rs.getString("Titolo"));
                t.setDescrizione(rs.getString("Descrizione"));
                t.setStato(rs.getString("Stato"));
                t.setIdAutore(rs.getLong("ID_Autore"));
                t.setDataApertura(rs.getTimestamp("DataApertura"));

                lista.add(t);
            }
        }
        return lista;
    }

    /* ==========================================================
       AGGIORNAMENTO STATO TICKET (SUPERVISORE)
       ========================================================== */

    public void updateStato(long idTicket, String nuovoStato)
            throws Exception {

        String sql = """
            UPDATE Ticket
            SET Stato = ?
            WHERE ID_Ticket = ?
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuovoStato);
            ps.setLong(2, idTicket);
            ps.executeUpdate();
        }
    }

    /* ==========================================================
       CANCELLAZIONE SICURA TICKET (SOLO AUTORE, SOLO APERTO)
       ========================================================== */
    public boolean deleteTicketIfAperto(long idTicket, long idAutore)
            throws Exception {

        String sql = """
            DELETE FROM Ticket
            WHERE ID_Ticket = ?
              AND ID_Autore = ?
              AND Stato = 'APERTO'
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTicket);
            ps.setLong(2, idAutore);

            return ps.executeUpdate() > 0;
        }
    }

    public Ticket doRetrieveByIdAndUtente(long idTicket, long idUtente) {

        String sql = """
        SELECT *
        FROM ticket
        WHERE ID_Ticket = ?
          AND ID_Autore = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTicket);
            ps.setLong(2, idUtente);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ticket t = new Ticket();
                t.setIdTicket(rs.getLong("ID_Ticket"));
                t.setTitolo(rs.getString("Titolo"));
                t.setDescrizione(rs.getString("Descrizione"));
                t.setCategoria(rs.getString("Categoria"));
                t.setPriorita(rs.getString("Priorita"));
                t.setStato(rs.getString("Stato"));
                t.setDataApertura(rs.getTimestamp("DataApertura"));
                return t;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Ticket doRetrieveByIdWithAutore(long idTicket) {

        String sql = """
        SELECT t.*, u.Nome, u.Cognome
        FROM ticket t
        JOIN utente u ON t.ID_Autore = u.ID_Utente
        WHERE t.ID_Ticket = ?
    """;

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
                t.setDataApertura(rs.getTimestamp("DataApertura"));

                //info autore (SOLO per supervisore)
                t.setNomeAutore(rs.getString("Nome"));
                t.setCognomeAutore(rs.getString("Cognome"));

                return t;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
