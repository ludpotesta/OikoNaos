package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.RichiestaRisorsa;
import util.database;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RichiestaRisorsaDAO {

    public void creaRichiesta(RichiestaRisorsa r) throws Exception {
        String sql = " INSERT INTO RichiestaRisorsa(DataInizio, DataFine, Stato, AccettazioneRegole, ID_Utente, ID_Risorsa) VALUES (?, ?, 'RICHIESTA', ?, ?, ?)";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(r.getDataInizio()));
            ps.setTimestamp(2, Timestamp.valueOf(r.getDataFine()));
            ps.setBoolean(3, r.getAccettazioneRegole());
            ps.setLong(4, r.getIdUtente());
            ps.setLong(5, r.getIdRisorsa());

            ps.executeUpdate();
        }
    }

    public List<RichiestaRisorsa> doRetrieveAll() throws Exception {
        List<RichiestaRisorsa> lista = new ArrayList<>();

        String sql = " SELECT rr.*, r.Nome AS NomeRisorsa, u.Nome AS NomeUtente, u.Cognome FROM RichiestaRisorsa rr JOIN RisorsaCondivisa r ON rr.ID_Risorsa = r.ID_Risorsa JOIN Utente u ON rr.ID_Utente = u.ID_Utente ORDER BY rr.DataInizio DESC ";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RichiestaRisorsa r = new RichiestaRisorsa();
                r.setIdRichiesta(rs.getLong("ID_Richiesta"));
                r.setDataInizio(rs.getTimestamp("DataInizio"));
                r.setDataFine(rs.getTimestamp("DataFine"));
                r.setStato(rs.getString("Stato"));
                r.setNomeRisorsa(rs.getString("NomeRisorsa"));
                r.setNomeUtente(
                        rs.getString("NomeUtente") + " " + rs.getString("Cognome")
                );
                lista.add(r);
            }
        }
        return lista;
    }


    public List<RichiestaRisorsa> doRetrieveByUtente(long idUtente) throws Exception {
        List<RichiestaRisorsa> lista = new ArrayList<>();

        String sql = """
        SELECT rr.*, r.Nome
        FROM RichiestaRisorsa rr
        JOIN RisorsaCondivisa r ON rr.ID_Risorsa = r.ID_Risorsa
        WHERE rr.ID_Utente = ?
        ORDER BY rr.DataInizio DESC
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RichiestaRisorsa r = new RichiestaRisorsa();
                r.setIdRichiesta(rs.getLong("ID_Richiesta"));
                r.setDataInizio(rs.getTimestamp("DataInizio"));
                r.setDataFine(rs.getTimestamp("DataFine"));
                r.setStato(rs.getString("Stato"));
                r.setNomeRisorsa(rs.getString("Nome"));
                lista.add(r);
            }
        }
        return lista;
    }

    public void aggiornaStato(long idRichiesta, String stato) throws Exception {
        String sql = " UPDATE RichiestaRisorsa SET Stato = ? WHERE ID_Richiesta = ? ";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, stato);
            ps.setLong(2, idRichiesta);
            ps.executeUpdate();
        }
    }

    // Metodo per il controllo del conflitto tra le richieste
    public boolean esisteConflitto(
            long idRisorsa,
            LocalDateTime inizio,
            LocalDateTime fine
    ) throws Exception {

        String sql = """
        SELECT COUNT(*)
        FROM RichiestaRisorsa
        WHERE ID_Risorsa = ?
          AND Stato = 'APPROVATA'
          AND (
                (? BETWEEN DataInizio AND DataFine)
             OR (? BETWEEN DataInizio AND DataFine)
             OR (DataInizio BETWEEN ? AND ?)
          )
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idRisorsa);
            ps.setTimestamp(2, Timestamp.valueOf(inizio));
            ps.setTimestamp(3, Timestamp.valueOf(fine));
            ps.setTimestamp(4, Timestamp.valueOf(inizio));
            ps.setTimestamp(5, Timestamp.valueOf(fine));

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }


}

