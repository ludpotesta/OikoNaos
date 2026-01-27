package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.dto.EventoBachecaDTO;
import it.unisa.oikonaos.model.Evento;
import util.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {

    public List<EventoBachecaDTO> getEventiBacheca(long idUtente) throws Exception {

        String sql = """
        SELECT 
            e.ID_Evento,
            e.Titolo,
            e.Descrizione,
            e.Luogo,
            e.DataInizio,
            e.DataFine,
            e.PostiDisponibili,
            CASE 
                WHEN ie.ID_Iscrizione IS NULL THEN false
                ELSE true
            END AS Iscritto
        FROM evento e
        LEFT JOIN iscrizioneevento ie
            ON e.ID_Evento = ie.ID_Evento
           AND ie.ID_Utente = ?
        ORDER BY e.DataInizio ASC
    """;

        List<EventoBachecaDTO> eventi = new ArrayList<>();

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    EventoBachecaDTO e = new EventoBachecaDTO();

                    e.setIdEvento(rs.getLong("ID_Evento"));
                    e.setTitolo(rs.getString("Titolo"));
                    e.setDescrizione(rs.getString("Descrizione"));
                    e.setLuogo(rs.getString("Luogo"));

                    e.setDataInizio(
                            rs.getTimestamp("DataInizio").toLocalDateTime()
                    );

                    Timestamp fine = rs.getTimestamp("DataFine");
                    if (fine != null) {
                        e.setDataFine(fine.toLocalDateTime());
                    } else {
                        e.setDataFine(null);
                    }

                    e.setPostiDisponibili(rs.getInt("PostiDisponibili"));
                    e.setIscritto(rs.getBoolean("Iscritto"));

                    eventi.add(e);
                }
            }
        }

        return eventi;
    }

    public void iscriviUtenteEvento(long idUtente, long idEvento) throws Exception {

        String checkSql = """
        SELECT PostiDisponibili
        FROM evento
        WHERE ID_Evento = ?
        FOR UPDATE
    """;

        String insertSql = """
        INSERT INTO iscrizioneevento (ID_Utente, ID_Evento)
        VALUES (?, ?)
    """;

        String updateSql = """
        UPDATE evento
        SET PostiDisponibili = PostiDisponibili - 1
        WHERE ID_Evento = ?
    """;

        try (Connection con = database.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement check = con.prepareStatement(checkSql)) {
                check.setLong(1, idEvento);
                ResultSet rs = check.executeQuery();

                if (!rs.next() || rs.getInt("PostiDisponibili") <= 0) {
                    throw new IllegalStateException("Posti esauriti");
                }
            }

            try (PreparedStatement ins = con.prepareStatement(insertSql)) {
                ins.setLong(1, idUtente);
                ins.setLong(2, idEvento);
                ins.executeUpdate();
            }

            try (PreparedStatement upd = con.prepareStatement(updateSql)) {
                upd.setLong(1, idEvento);
                upd.executeUpdate();
            }

            con.commit();
        }
    }

    public EventoBachecaDTO getEventoById(long idEvento, long idUtente) throws Exception {

        String sql = """
        SELECT e.ID_Evento, e.Titolo, e.Descrizione, e.Luogo,
               e.DataInizio, e.PostiDisponibili,
               CASE WHEN ie.ID_Iscrizione IS NULL THEN false ELSE true END AS Iscritto
        FROM evento e
        LEFT JOIN iscrizioneevento ie
               ON e.ID_Evento = ie.ID_Evento AND ie.ID_Utente = ?
        WHERE e.ID_Evento = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idUtente);
            ps.setLong(2, idEvento);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return null;

                EventoBachecaDTO e = new EventoBachecaDTO();
                e.setIdEvento(rs.getLong("ID_Evento"));
                e.setTitolo(rs.getString("Titolo"));
                e.setDescrizione(rs.getString("Descrizione"));
                e.setLuogo(rs.getString("Luogo"));
                e.setDataInizio(rs.getTimestamp("DataInizio").toLocalDateTime());
                e.setPostiDisponibili(rs.getInt("PostiDisponibili"));
                e.setIscritto(rs.getBoolean("Iscritto"));

                return e;
            }
        }
    }


    public Evento getEventoById(long idEvento) throws Exception {

        String sql = "SELECT * FROM evento WHERE ID_Evento = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idEvento);

            try (ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) return null;

                Evento e = new Evento();
                e.setIdEvento(rs.getLong("ID_Evento"));
                e.setTitolo(rs.getString("Titolo"));
                e.setDescrizione(rs.getString("Descrizione"));
                e.setLuogo(rs.getString("Luogo"));
                e.setDataInizio(rs.getTimestamp("DataInizio").toLocalDateTime());

                Timestamp fine = rs.getTimestamp("DataFine");
                if (fine != null) {
                    e.setDataFine(fine.toLocalDateTime());
                }

                e.setPostiTotali(rs.getInt("PostiTotali"));
                e.setPostiDisponibili(rs.getInt("PostiDisponibili"));
                e.setIdOrganizzatore(rs.getLong("ID_Organizzatore"));

                return e;
            }
        }
    }

    public void disiscriviUtenteDaEvento(long idEvento, long idUtente) throws Exception {

        String deleteSql = """
        DELETE FROM iscrizioneevento
        WHERE ID_Evento = ? AND ID_Utente = ?
    """;

        String updateSql = """
        UPDATE evento
        SET PostiDisponibili = PostiDisponibili + 1
        WHERE ID_Evento = ?
    """;

        try (Connection con = database.getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement psDelete = con.prepareStatement(deleteSql);
                 PreparedStatement psUpdate = con.prepareStatement(updateSql)) {

                psDelete.setLong(1, idEvento);
                psDelete.setLong(2, idUtente);
                psDelete.executeUpdate();

                psUpdate.setLong(1, idEvento);
                psUpdate.executeUpdate();

                con.commit();
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            }
        }
    }

    public List<Evento> getAllEventi() throws Exception {

        List<Evento> eventi = new ArrayList<>();

        String sql = """
        SELECT
            ID_Evento,
            Titolo,
            Descrizione,
            Luogo,
            DataInizio,
            DataFine,
            PostiTotali,
            PostiDisponibili,
            ID_Organizzatore
        FROM evento
        ORDER BY DataInizio DESC
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Evento e = new Evento();

                e.setIdEvento(rs.getLong("ID_Evento"));
                e.setTitolo(rs.getString("Titolo"));
                e.setDescrizione(rs.getString("Descrizione"));
                e.setLuogo(rs.getString("Luogo"));

                e.setDataInizio(
                        rs.getTimestamp("DataInizio").toLocalDateTime()
                );

                Timestamp dataFine = rs.getTimestamp("DataFine");
                if (dataFine != null) {
                    e.setDataFine(dataFine.toLocalDateTime());
                }

                e.setPostiTotali(rs.getInt("PostiTotali"));
                e.setPostiDisponibili(rs.getInt("PostiDisponibili"));
                e.setIdOrganizzatore(rs.getLong("ID_Organizzatore"));

                eventi.add(e);
            }
        }

        return eventi;
    }

    public void creaEvento(Evento e) throws Exception {

        String sql = """
        INSERT INTO evento
        (Titolo, Descrizione, Luogo, DataInizio, DataFine,
         PostiTotali, PostiDisponibili, ID_Organizzatore)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getTitolo());
            ps.setString(2, e.getDescrizione());
            ps.setString(3, e.getLuogo());
            ps.setTimestamp(4, Timestamp.valueOf(e.getDataInizio()));

            if (e.getDataFine() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(e.getDataFine()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setInt(6, e.getPostiTotali());
            ps.setInt(7, e.getPostiDisponibili());
            ps.setLong(8, e.getIdOrganizzatore());

            ps.executeUpdate();
        }
    }

    public void aggiornaEvento(Evento e) throws Exception {

        String sql = """
        UPDATE evento
        SET Titolo = ?, Descrizione = ?, Luogo = ?,
            DataInizio = ?, DataFine = ?, PostiTotali = ?
        WHERE ID_Evento = ?
    """;

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getTitolo());
            ps.setString(2, e.getDescrizione());
            ps.setString(3, e.getLuogo());
            ps.setTimestamp(4, Timestamp.valueOf(e.getDataInizio()));

            if (e.getDataFine() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(e.getDataFine()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            ps.setInt(6, e.getPostiTotali());
            ps.setLong(7, e.getIdEvento());

            ps.executeUpdate();
        }
    }

    public void eliminaEvento(long idEvento) throws Exception {

        String sql = "DELETE FROM evento WHERE ID_Evento = ?";

        try (Connection con = database.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, idEvento);
            ps.executeUpdate();
        }
    }
}
