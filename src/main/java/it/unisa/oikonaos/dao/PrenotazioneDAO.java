package it.unisa.oikonaos.dao;

import util.database;
import it.unisa.oikonaos.model.Prenotazione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrenotazioneDAO {

    public void creaPrenotazione(Prenotazione p) throws Exception {
        String sql = "INSERT INTO prenotazione (DataPrenotazione, Stato, ID_Utente, ID_Postazione, ID_Fascia) VALUES (?, ?, ?, ?, ?)";

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

    public boolean verificaConflitto(Date data, long idPostazione, long idFascia) throws Exception {
        String sql = "SELECT COUNT(*) FROM prenotazione WHERE DataPrenotazione = ? AND ID_Postazione = ? AND ID_Fascia = ? AND Stato = 'ATTIVA'";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, data);
            ps.setLong(2, idPostazione);
            ps.setLong(3, idFascia);

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<Prenotazione> doRetrieveByUtente(long idUtente) throws Exception {
        List<Prenotazione> lista = new ArrayList<>();

        String sql = "SELECT pr.ID_Prenotazione, pr.DataPrenotazione, pr.Stato, po.Numero AS NumeroPostazione, a.Nome AS NomeAmbiente, f.OraInizio, f.OraFine FROM prenotazione pr JOIN postazione po ON pr.ID_Postazione = po.ID_Postazione JOIN ambiente a ON po.ID_Ambiente = a.ID_Ambiente JOIN fasciaoraria f ON pr.ID_Fascia = f.ID_Fascia WHERE pr.ID_Utente = ? ORDER BY pr.DataPrenotazione DESC";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setIdPrenotazione(rs.getLong("ID_Prenotazione"));
                p.setData(rs.getDate("DataPrenotazione"));
                p.setStato(rs.getString("Stato"));
                //p.setIdUtente(rs.getLong("ID_Utente"));
                p.setNumeroPostazione(rs.getString("NumeroPostazione"));
                p.setNomeAmbiente(rs.getString("NomeAmbiente"));
                p.setOrarioInizio(rs.getTime("OraInizio"));
                p.setOrarioFine(rs.getTime("OraFine"));
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> doRetrieveAll() throws Exception {
        List<Prenotazione> lista = new ArrayList<>();

        String sql = "SELECT * FROM prenotazione";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setIdPrenotazione(rs.getLong("ID_Prenotazione"));
                p.setData(rs.getDate("DataPrenotazione"));
                p.setIdUtente(rs.getLong("ID_Utente"));
                p.setIdPostazione(rs.getLong("ID_Postazione"));
                p.setIdFasciaOraria(rs.getLong("ID_Fascia"));
                lista.add(p);
            }
        }
        return lista;
    }

    public void doDelete(long idPrenotazione) throws Exception {
        String sql = "DELETE FROM prenotazione WHERE ID_Prenotazione = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPrenotazione);
            ps.executeUpdate();
        }
    }

    public List<Prenotazione> findAllByComunita(long idComunita) throws SQLException {

        String sql = """
        SELECT p.*, u.Nome, u.Cognome, po.Numero, a.Nome AS NomeAmbiente, f.OraInizio, f.OraFine
        FROM Prenotazione p
        JOIN Utente u ON p.ID_Utente = u.ID_Utente
        JOIN Postazione po ON p.ID_Postazione = po.ID_Postazione
        JOIN Ambiente a ON po.ID_Ambiente = a.ID_Ambiente
        JOIN FasciaOraria f ON p.ID_Fascia = f.ID_Fascia
        WHERE u.ID_Comunita = ?
        ORDER BY p.DataPrenotazione DESC
        """;


        List<Prenotazione> list = new ArrayList<>();

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idComunita);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Prenotazione p = new Prenotazione();
                p.setIdPrenotazione(rs.getLong("ID_Prenotazione"));
                p.setData(rs.getDate("DataPrenotazione"));
                p.setStato(rs.getString("Stato"));
                p.setIdUtente(Long.parseLong(rs.getString("ID_Utente")));
                p.setIdPostazione(Long.parseLong(rs.getString("ID_Postazione")));
                p.setOrarioInizio(rs.getTime("OraInizio"));
                p.setOrarioFine(rs.getTime("OraFine"));
                list.add(p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
