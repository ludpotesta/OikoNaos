package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Risorsa;
import util.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RisorsaDAO {

    public void doSave(Risorsa r) throws Exception {

        String sql = """
            INSERT INTO RisorsaCondivisa
            (Nome, Descrizione, RegoleUso, Penale)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, r.getNome());
            ps.setString(2, r.getDescrizione());
            ps.setString(3, r.getRegoleUso());

            if (r.getPenale() != null) {
                ps.setBigDecimal(4, r.getPenale());
            } else {
                ps.setNull(4, Types.DECIMAL);
            }

            ps.executeUpdate();
        }
    }

    public List<Risorsa> doRetrieveAll() throws Exception {

        List<Risorsa> lista = new ArrayList<>();

        String sql = """
            SELECT ID_Risorsa, Nome, Descrizione, RegoleUso, Penale
            FROM RisorsaCondivisa
        """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Risorsa r = new Risorsa();
                r.setIdRisorsa(rs.getLong("ID_Risorsa"));
                r.setNome(rs.getString("Nome"));
                r.setDescrizione(rs.getString("Descrizione"));
                r.setRegoleUso(rs.getString("RegoleUso"));
                r.setPenale(rs.getBigDecimal("Penale"));
                lista.add(r);
            }
        }
        return lista;
    }
}
