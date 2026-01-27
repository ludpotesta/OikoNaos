package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.RichiestaRisorsa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RichiestaRisorsaDAOTest {

    private RichiestaRisorsaDAO richiestaDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        richiestaDAO = new RichiestaRisorsaDAO();
    }

    @Test
    void testCreaRichiesta_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        Date start = Date.valueOf("2026-05-01");
        Date end = Date.valueOf("2026-05-05");

        // ACT
        richiestaDAO.creaRichiesta(mockConnection, 1L, 10L, start, end);

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setDate(3, start);
        verify(mockPreparedStatement).setDate(4, end);
    }

    @Test
    void testDoRetrieveByUtente_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false); // 1 risultato

        // Mock dei campi della JOIN
        when(mockResultSet.getLong("ID_Richiesta")).thenReturn(50L);
        when(mockResultSet.getTimestamp("DataInizio")).thenReturn(Timestamp.valueOf("2026-05-01 10:00:00"));
        when(mockResultSet.getTimestamp("DataFine")).thenReturn(Timestamp.valueOf("2026-05-01 12:00:00"));
        when(mockResultSet.getString("Stato")).thenReturn("APPROVATA");
        when(mockResultSet.getString("NomeRisorsa")).thenReturn("Proiettore");

        // ACT
        List<RichiestaRisorsa> lista = richiestaDAO.doRetrieveByUtente(mockConnection, 10L);

        // ASSERT
        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Proiettore", lista.get(0).getNomeRisorsa());
        assertEquals("APPROVATA", lista.get(0).getStato());
    }

    @Test
    void testAggiornaStato() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        richiestaDAO.aggiornaStato(mockConnection, 5L, "RIFIUTATA");

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setString(1, "RIFIUTATA");
        verify(mockPreparedStatement).setLong(2, 5L);
    }

    @Test
    void testEsisteConflitto_True() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo che la COUNT(*) ritorni un valore > 0
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(3); // 3 richieste già presenti quel giorno

        // ACT
        boolean result = richiestaDAO.esisteConflitto(mockConnection, 1L, LocalDate.of(2026, 5, 1));

        // ASSERT
        assertTrue(result, "Deve rilevare il conflitto se count > 0");
    }

    @Test
    void testEsisteConflitto_False() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo COUNT(*) = 0
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0);

        // ACT
        boolean result = richiestaDAO.esisteConflitto(mockConnection, 1L, LocalDate.of(2026, 5, 1));

        // ASSERT
        assertFalse(result, "Nessun conflitto se count == 0");
    }

    @Test
    void testGetDateOccupate() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo 2 date trovate
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getDate(1)).thenReturn(
                Date.valueOf("2026-06-01"),
                Date.valueOf("2026-06-02")
        );

        // ACT
        List<LocalDate> date = richiestaDAO.getDateOccupate(mockConnection, 1L);

        // ASSERT
        assertEquals(2, date.size());
        assertEquals(LocalDate.of(2026, 6, 1), date.get(0));
        assertEquals(LocalDate.of(2026, 6, 2), date.get(1));
    }
}