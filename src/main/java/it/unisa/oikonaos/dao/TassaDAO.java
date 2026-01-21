package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.TassaTrimestrale;
import util.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import java.util.List;

public class TassaDAO {

    public List<TassaTrimestrale> getTasseByUtente(long idUtente) {

        List<TassaTrimestrale> tasse = new ArrayList<>();

        String sql = """
                SELECT t.*, p.ID_Pagamento, r.ID_Ricevuta
                          FROM tassatrimestrale t
                          LEFT JOIN pagamento p\s
                                 ON t.ID_Tassa = p.ID_Tassa
                                AND p.ID_Utente = ?        -- 🔥 FIX
                          LEFT JOIN ricevuta r\s
                                 ON p.ID_Pagamento = r.ID_Pagamento
                          WHERE t.ID_Utente = ?
                             OR t.ID_Utente IS NULL
                          ORDER BY t.Scadenza DESC
            """;


        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ps.setLong(2, idUtente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                TassaTrimestrale t = new TassaTrimestrale();

                t.setIdTassa(rs.getLong("ID_Tassa"));
                t.setTrimestreRiferimento(rs.getString("TrimestreRiferimento"));
                t.setImportoDovuto(rs.getBigDecimal("ImportoDovuto"));
                t.setScadenza(rs.getDate("Scadenza").toLocalDate());
                t.setStato(rs.getString("Stato"));
                t.setIdPagamento(rs.getObject("ID_Pagamento", Long.class));
                t.setHasRicevuta(rs.getObject("ID_Ricevuta") != null);

                tasse.add(t);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return tasse;
    }

    public void marcaComePagata(long idTassa) {

        String sql = "UPDATE tassatrimestrale SET Stato = 'PAGATA' WHERE ID_Tassa = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTassa);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void creaTassa(String trimestre,
                          double importo,
                          Date scadenza,
                          String tipo,
                          Long idUtente) throws Exception {

        String sql = """
        INSERT INTO tassatrimestrale
        (TrimestreRiferimento, ImportoDovuto, Scadenza, Tipo, ID_Utente)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, trimestre);
            ps.setDouble(2, importo);
            ps.setDate(3, scadenza);
            ps.setString(4, tipo);

            if (idUtente != null) {
                ps.setLong(5, idUtente);
            } else {
                ps.setNull(5, java.sql.Types.BIGINT);
            }

            ps.executeUpdate();
        }
    }

    public List<TassaTrimestrale> doRetrieveAll() {

        List<TassaTrimestrale> tasse = new ArrayList<>();

        String sql = """
           SELECT t.*, u.Nome, u.Cognome, p.ID_Pagamento
           FROM tassatrimestrale t
           LEFT JOIN utente u ON t.ID_Utente = u.ID_Utente
           LEFT JOIN pagamento p ON t.ID_Tassa = p.ID_Tassa
           ORDER BY t.Scadenza DESC
        """;


        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TassaTrimestrale t = new TassaTrimestrale();

                t.setIdTassa(rs.getLong("ID_Tassa"));
                t.setTrimestreRiferimento(rs.getString("TrimestreRiferimento"));
                t.setImportoDovuto(rs.getBigDecimal("ImportoDovuto"));
                t.setScadenza(rs.getDate("Scadenza").toLocalDate());
                t.setIdPagamento(rs.getObject("ID_Pagamento", Long.class));
                t.setPagata(rs.getObject("ID_Pagamento") != null);
                t.setIdUtente(rs.getObject("ID_Utente", Long.class));
                t.setNomeUtente(rs.getString("Nome"));
                t.setCognomeUtente(rs.getString("Cognome"));

                tasse.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tasse;
    }

    public TassaTrimestrale getTassaById(long idTassa) {

        String sql = """
        SELECT t.*
        FROM tassatrimestrale t
        WHERE t.ID_Tassa = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idTassa);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                TassaTrimestrale t = new TassaTrimestrale();

                t.setIdTassa(rs.getLong("ID_Tassa"));
                t.setTrimestreRiferimento(rs.getString("TrimestreRiferimento"));
                t.setImportoDovuto(rs.getBigDecimal("ImportoDovuto"));
                t.setScadenza(rs.getDate("Scadenza").toLocalDate());
                t.setStato(rs.getString("Stato"));
                t.setIdUtente(rs.getObject("ID_Utente", Long.class));

                return t;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
