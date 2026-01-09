package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Ricevuta;
import util.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Optional;

public class RicevutaDAO {

    public void creaRicevuta(long idPagamento, double importo) {

        String sql = """
            INSERT INTO ricevuta (ID_Pagamento, Importo, DataEmissione)
            VALUES (?, ?, CURRENT_TIMESTAMP)
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPagamento);
            ps.setDouble(2, importo);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Optional<Ricevuta> getRicevutaByPagamento(long idPagamento) {

        String sql = """
            SELECT *
            FROM ricevuta
            WHERE ID_Pagamento = ?
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idPagamento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Ricevuta r = new Ricevuta();
                r.setIdRicevuta(rs.getLong("ID_Ricevuta"));
                r.setIdPagamento(idPagamento);
                r.setImporto(rs.getBigDecimal("Importo"));
                r.setDataEmissione(
                        rs.getTimestamp("DataEmissione").toLocalDateTime()
                );
                return Optional.of(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}

