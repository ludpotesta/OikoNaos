package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Ricevuta;
import util.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;

public class RicevutaDAO {

    public void creaRicevuta(long idPagamento) {
        String checkSql = """
            SELECT 1
            FROM ricevuta
            WHERE ID_Pagamento = ?
        """;

        String insertSql = """
            INSERT INTO ricevuta (ID_Pagamento, CodiceTransazione)
            SELECT ID_Pagamento, ?
            FROM pagamento
            WHERE ID_Pagamento = ?
              AND DataPagamento IS NOT NULL
        """;

        try (Connection con = Database.getConnection()) {

            // Controllo duplicati
            try (PreparedStatement ps = con.prepareStatement(checkSql)) {
                ps.setLong(1, idPagamento);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return; // ricevuta già esistente
                }
            }

            // Creazione ricevuta
            try (PreparedStatement ps = con.prepareStatement(insertSql)) {
                ps.setString(1, "TX-" + System.currentTimeMillis());
                ps.setLong(2, idPagamento);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<Ricevuta> getRicevutaByPagamento(long idPagamento) {

        String sql = """
            SELECT r.ID_Ricevuta,
                   r.CodiceTransazione,
                   r.DataEmissione,
                   p.ImportoPagato
            FROM ricevuta r
            JOIN pagamento p ON r.ID_Pagamento = p.ID_Pagamento
            WHERE r.ID_Pagamento = ?
        """;

        try (Connection con = Database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPagamento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ricevuta r = new Ricevuta();
                r.setIdRicevuta(rs.getLong("ID_Ricevuta"));
                r.setCodiceTransazione(rs.getString("CodiceTransazione"));
                r.setImporto(rs.getBigDecimal("ImportoPagato"));
                r.setDataEmissione(rs.getTimestamp("DataEmissione").toLocalDateTime());
                return Optional.of(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
