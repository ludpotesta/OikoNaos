package it.unisa.oikonaos.dao;

import util.database;
import it.unisa.oikonaos.model.Prenotazione;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PrenotazioneDAO {

    /* CREAZIONE PRENOTAZIONE */
    public void creaPrenotazione(Prenotazione p) throws Exception {
        String sql = """
            INSERT INTO prenotazione
            (DataPrenotazione, Stato, ID_Utente, ID_Postazione, ID_Fascia)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, p.getData());
            ps.setString(2, p.getStato());
            ps.setLong(3, p.getIdUtente());
            ps.setLong(4, p.getIdPostazione());
            ps.setLong(5, p.getIdFasciaOraria());

            ps.executeUpdate();
        }
    }

    public boolean verificaConflitto(Date dataPrenotazione,
                                     long idPostazione,
                                     long idFascia) throws Exception {

        String sql = """
        SELECT COUNT(*)
        FROM prenotazione
        WHERE DataPrenotazione = ?
          AND ID_Postazione = ?
          AND ID_Fascia = ?
          AND Stato = 'ATTIVA'
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, dataPrenotazione);
            ps.setLong(2, idPostazione);
            ps.setLong(3, idFascia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    public List<Prenotazione> doRetrieveByUtente(long idUtente)
            throws Exception {

        List<Prenotazione> lista = new ArrayList<>();

        String sql = """
            SELECT
                pr.ID_Prenotazione,
                pr.DataPrenotazione,
                pr.Stato,
                po.Numero        AS NumeroPostazione,
                a.Nome           AS NomeAmbiente,
                f.OraInizio,
                f.OraFine
            FROM prenotazione pr
            JOIN postazione po ON pr.ID_Postazione = po.ID_Postazione
            JOIN ambiente a    ON po.ID_Ambiente = a.ID_Ambiente
            JOIN fasciaoraria f ON pr.ID_Fascia = f.ID_Fascia
            WHERE pr.ID_Utente = ?
            ORDER BY pr.DataPrenotazione DESC
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prenotazione p = new Prenotazione();
                    p.setIdPrenotazione(rs.getLong("ID_Prenotazione"));
                    p.setData(rs.getDate("DataPrenotazione"));
                    p.setStato(rs.getString("Stato"));
                    p.setNumeroPostazione(rs.getString("NumeroPostazione"));
                    p.setNomeAmbiente(rs.getString("NomeAmbiente"));
                    p.setOrarioInizio(rs.getTime("OraInizio"));
                    p.setOrarioFine(rs.getTime("OraFine"));

                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public List<Prenotazione> doRetrieveAll()
            throws Exception {

        List<Prenotazione> lista = new ArrayList<>();

        String sql = """
            SELECT
                pr.ID_Prenotazione,
                pr.DataPrenotazione,
                pr.Stato,
                pr.ID_Utente,
                pr.ID_Postazione,
                pr.ID_Fascia,
                a.Nome AS NomeAmbiente
            FROM prenotazione pr
            JOIN postazione po ON pr.ID_Postazione = po.ID_Postazione
            JOIN ambiente a    ON po.ID_Ambiente = a.ID_Ambiente
            ORDER BY pr.DataPrenotazione DESC
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setIdPrenotazione(rs.getLong("ID_Prenotazione"));
                p.setData(rs.getDate("DataPrenotazione"));
                p.setStato(rs.getString("Stato"));
                p.setIdUtente(rs.getLong("ID_Utente"));
                p.setIdPostazione(rs.getLong("ID_Postazione"));
                p.setIdFasciaOraria(rs.getLong("ID_Fascia"));
                p.setNomeAmbiente(rs.getString("NomeAmbiente"));

                lista.add(p);
            }
        }
        return lista;
    }

    public Prenotazione doRetrieveById(long idPrenotazione)
            throws Exception {

        String sql = """
        SELECT
            ID_Prenotazione,
            DataPrenotazione,
            Stato,
            ID_Utente
        FROM prenotazione
        WHERE ID_Prenotazione = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPrenotazione);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prenotazione p = new Prenotazione();
                    p.setIdPrenotazione(rs.getLong("ID_Prenotazione"));
                    p.setData(rs.getDate("DataPrenotazione"));
                    p.setStato(rs.getString("Stato"));
                    p.setIdUtente(rs.getLong("ID_Utente"));
                    return p;
                }
            }
        }
        return null;
    }

    /* CANCELLAZIONE SICURA PRENOTAZIONE*/
    public boolean doDelete(long idPrenotazione, long idUtente)
            throws Exception {

        String sql = """
            DELETE FROM prenotazione
            WHERE ID_Prenotazione = ?
              AND ID_Utente = ?
              AND Stato = 'ATTIVA'
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPrenotazione);
            ps.setLong(2, idUtente);

            return ps.executeUpdate() > 0;
        }
    }

    public List<Object[]> doRetrieveAmbienti() throws Exception {

        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT ID_Ambiente, Nome FROM ambiente ORDER BY Nome";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getLong("ID_Ambiente"),
                        rs.getString("Nome")
                });
            }
        }
        return lista;
    }

    public List<long[]> doRetrievePostazioniByAmbiente(long idAmbiente) throws Exception {

        List<long[]> lista = new ArrayList<>();

        String sql = """
        SELECT ID_Postazione, Numero
        FROM postazione
        WHERE ID_Ambiente = ?
        ORDER BY Numero
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idAmbiente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new long[]{
                            rs.getLong("ID_Postazione"),
                            rs.getLong("Numero")
                    });
                }
            }
        }
        return lista;
    }
}
