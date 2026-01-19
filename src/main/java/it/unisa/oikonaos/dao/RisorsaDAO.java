package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Risorsa;
import util.database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RisorsaDAO {

    public List<Risorsa> doRetrieveAll() throws Exception {
        List<Risorsa> lista = new ArrayList<>();

        String sql = " SELECT ID_Risorsa, Nome, Descrizione, RegoleUso, Penale FROM RisorsaCondivisa ";

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

    public List<Risorsa> doRetrieveDisponibili() throws Exception {
        List<Risorsa> lista = new ArrayList<>();

        String sql = " SELECT r.* FROM RisorsaCondivisa r WHERE r.ID_Risorsa NOT IN (SELECT ID_Risorsa FROM RichiestaRisorsa) ";

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

