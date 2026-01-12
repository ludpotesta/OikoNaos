package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Pagamento;
import util.database;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    public List<Pagamento> getPagamentiByUtente(long idUtente) {

        List<Pagamento> pagamenti = new ArrayList<>();

        String sql = "SELECT p.ID_Pagamento, p.ImportoPagato, p.DataPagamento, p.MetodoPagamento, p.ID_Tassa, t.TrimestreRiferimento, t.Scadenza FROM pagamento p JOIN tassatrimestrale t ON p.ID_Tassa = t.ID_Tassa\n WHERE p.ID_Utente = ?\n ORDER BY t.Scadenza DESC";

        System.out.println("SQL PAGAMENTI:\n" + sql);
        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Pagamento p = new Pagamento();

                p.setIdPagamento(rs.getLong("ID_Pagamento"));
                p.setIdUtente(idUtente);
                p.setIdTassa(rs.getLong("ID_Tassa"));
                p.setImportoPagato(rs.getBigDecimal("ImportoPagato"));
                p.setMetodoPagamento(rs.getString("MetodoPagamento"));

                p.setPeriodo(rs.getString("TrimestreRiferimento"));
                p.setDataScadenza(rs.getDate("Scadenza").toLocalDate());

                Timestamp ts = rs.getTimestamp("DataPagamento");
                if (ts != null) {
                    p.setDataPagamento(ts.toLocalDateTime());
                }

                pagamenti.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pagamenti;
    }

    public void registraPagamentoOnline(long idPagamento, String metodo) {

        String sql = "UPDATE pagamento SET DataPagamento = CURRENT_TIMESTAMP, MetodoPagamento = ? WHERE ID_Pagamento = ? AND DataPagamento IS NULL";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, metodo);
            ps.setLong(2, idPagamento);
            ps.executeUpdate();

            new RicevutaDAO().creaRicevuta(idPagamento);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Pagamento getPagamentoById(long idPagamento) {

        String sql = """
        SELECT p.ID_Pagamento,
               p.ID_Tassa,
               p.ImportoPagato,
               p.DataPagamento,
               p.MetodoPagamento,
               t.TrimestreRiferimento,
               t.Scadenza
        FROM pagamento p
        LEFT JOIN tassatrimestrale t ON p.ID_Tassa = t.ID_Tassa
        WHERE p.ID_Pagamento = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPagamento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Pagamento p = new Pagamento();

                p.setIdPagamento(rs.getLong("ID_Pagamento"));
                p.setIdTassa(rs.getLong("ID_Tassa"));
                p.setImportoPagato(rs.getBigDecimal("ImportoPagato"));
                p.setMetodoPagamento(rs.getString("MetodoPagamento"));
                p.setPeriodo(rs.getString("TrimestreRiferimento"));

                Date scad = rs.getDate("Scadenza");
                if (scad != null) {
                    p.setDataScadenza(scad.toLocalDate());
                }

                Timestamp ts = rs.getTimestamp("DataPagamento");
                if (ts != null) {
                    p.setDataPagamento(ts.toLocalDateTime());
                }

                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public long creaPagamentoDaTassa(long idTassa, long idUtente) {

        String checkSql = """
        SELECT ID_Pagamento
        FROM pagamento
        WHERE ID_Tassa = ? AND ID_Utente = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {

            checkPs.setLong(1, idTassa);
            checkPs.setLong(2, idUtente);

            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) {
                // pagamento già esistente → riusa
                return rs.getLong("ID_Pagamento");
            }

            String insertSql = """
            INSERT INTO pagamento (ImportoPagato, MetodoPagamento, ID_Tassa, ID_Utente)
            SELECT ImportoDovuto, 'ONLINE', ID_Tassa, ID_Utente
            FROM tassatrimestrale
            WHERE ID_Tassa = ? AND ID_Utente = ?
        """;

            try (PreparedStatement insertPs = con.prepareStatement(insertSql,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {

                insertPs.setLong(1, idTassa);
                insertPs.setLong(2, idUtente);
                insertPs.executeUpdate();

                ResultSet keys = insertPs.getGeneratedKeys();
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
