package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.RichiestaRisorsa;
import util.Database;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RichiestaRisorsaDAO {

    public void creaRichiesta(long idRisorsa,
                              long idUtente,
                              Date dataInizio,
                              Date dataFine) throws Exception {

        String sql = """
        INSERT INTO richiestarisorsa
        (ID_Risorsa, ID_Utente, DataInizio, DataFine, Stato)
        VALUES (?, ?, ?, ?, 'RICHIESTA')
    """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idRisorsa);
            ps.setLong(2, idUtente);
            ps.setDate(3, dataInizio);
            ps.setDate(4, dataFine);

            ps.executeUpdate();
        }
    }

    public List<RichiestaRisorsa> doRetrieveAll() throws Exception {
        List<RichiestaRisorsa> lista = new ArrayList<>();

        String sql = """
            SELECT rr.*,
                   r.Nome AS NomeRisorsa,
                   u.Nome AS NomeUtente,
                   u.Cognome
            FROM richiestarisorsa rr
            JOIN risorsacondivisa r ON rr.ID_Risorsa = r.ID_Risorsa
            JOIN utente u ON rr.ID_Utente = u.ID_Utente
            ORDER BY rr.DataInizio DESC
        """;

        try (Connection con = Database.getConnection();
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
            SELECT rr.ID_Richiesta,
                   rr.DataInizio,
                   rr.DataFine,
                   rr.Stato,
                   r.Nome AS NomeRisorsa
            FROM richiestarisorsa rr
            JOIN risorsacondivisa r ON rr.ID_Risorsa = r.ID_Risorsa
            WHERE rr.ID_Utente = ?
            ORDER BY rr.DataInizio DESC
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                RichiestaRisorsa req = new RichiestaRisorsa();
                req.setIdRichiesta(rs.getLong("ID_Richiesta"));
                req.setDataInizio(rs.getTimestamp("DataInizio"));
                req.setDataFine(rs.getTimestamp("DataFine"));
                req.setStato(rs.getString("Stato"));
                req.setNomeRisorsa(rs.getString("NomeRisorsa"));
                lista.add(req);
            }
        }
        return lista;
    }

    public void aggiornaStato(long idRichiesta, String stato) throws Exception {
        String sql = """
            UPDATE richiestarisorsa
            SET Stato = ?
            WHERE ID_Richiesta = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, stato);
            ps.setLong(2, idRichiesta);
            ps.executeUpdate();
        }
    }

    public boolean esisteConflitto(long idRisorsa, LocalDate giorno) throws Exception {

        String sql = """
            SELECT COUNT(*)
            FROM richiestarisorsa
            WHERE ID_Risorsa = ?
              AND DATE(DataInizio) = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idRisorsa);
            ps.setDate(2, Date.valueOf(giorno));

            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<LocalDate> getDateOccupate(long idRisorsa) throws Exception {
        List<LocalDate> date = new ArrayList<>();

        String sql = """
            SELECT DISTINCT DATE(DataInizio)
            FROM richiestarisorsa
            WHERE ID_Risorsa = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idRisorsa);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                date.add(rs.getDate(1).toLocalDate());
            }
        }
        return date;
    }
}
