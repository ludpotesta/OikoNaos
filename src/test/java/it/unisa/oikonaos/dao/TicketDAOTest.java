package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketDAOTest {

    private TicketDAO ticketDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        ticketDAO = new TicketDAO();
    }

    @Test
    void testCreaTicket_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(100L);

        // ACT
        long id = ticketDAO.creaTicket(mockConnection, "PC Rotto", "Non si accende", "IT", "ALTA", 5L);

        // ASSERT
        assertEquals(100L, id);
        verify(mockPreparedStatement).setString(1, "PC Rotto");
        verify(mockPreparedStatement).setLong(5, 5L);
    }

    @Test
    void testDeleteTicketIfAperto_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Cancellato

        // ACT
        boolean res = ticketDAO.deleteTicketIfAperto(mockConnection, 100L, 5L);

        // ASSERT
        assertTrue(res);
    }

    @Test
    void testDeleteTicketIfAperto_Fail_WrongStatusOrUser() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        // Restituisce 0 se il ticket non era aperto o l'utente non era l'autore
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // ACT
        boolean res = ticketDAO.deleteTicketIfAperto(mockConnection, 100L, 99L); // ID Autore errato

        // ASSERT
        assertFalse(res, "Non deve cancellare se le condizioni non sono soddisfatte");
    }

    @Test
    void testDoRetrieveFiltered_StatoAndPriorita() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Mockiamo il risultato
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getLong("ID_Ticket")).thenReturn(1L);

        // ACT
        // Filtriamo per Stato=APERTO e Priorità=ALTA, Data=null
        List<Ticket> result = ticketDAO.doRetrieveFiltered(mockConnection, "APERTO", "ALTA", null);

        // ASSERT
        assertNotNull(result);

        // Verifica che abbia impostato i parametri corretti nel PreparedStatement

        verify(mockPreparedStatement).setObject(1, "APERTO");
        verify(mockPreparedStatement).setObject(2, "ALTA");
        verify(mockPreparedStatement, never()).setObject(eq(3), any());
    }

    @Test
    void testDoRetrieveFiltered_OnlyDate() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // ACT
        // Filtriamo solo per Data, null gli altri
        ticketDAO.doRetrieveFiltered(mockConnection, null, null, "2026-05-20");

        // ASSERT
        // In questo caso, la data diventa il primo parametro aggiunto alla lista
        verify(mockPreparedStatement).setObject(1, Date.valueOf("2026-05-20"));
    }

    @Test
    void testDoRetrieveFiltered_AllNulls() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo che trovi 2 ticket in totale nel DB
        when(mockResultSet.next()).thenReturn(true, true, false);

        // ACT
        // Passiamo tutto NULL o stringhe vuote
        List<Ticket> result = ticketDAO.doRetrieveFiltered(mockConnection, null, "", null);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());

        // VERIFICA CRITICA:
        // Se i parametri sono null, il DAO NON deve aver chiamato setObject() con parametri
        // Deve aver eseguito solo "SELECT * FROM ticket WHERE 1=1"
        verify(mockPreparedStatement, never()).setObject(anyInt(), any());
    }
    @Test
    void testUpdateStato_TicketNotFound() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo che il DB dica "0 righe modificate"
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        // ACT
        // Proviamo ad aggiornare un ID assurdo
        assertDoesNotThrow(() -> {
            ticketDAO.updateStato(mockConnection, 99999L, "CHIUSO");
        });

        // ASSERT
        // Verifichiamo comunque che ci abbia provato
        verify(mockPreparedStatement).setLong(2, 99999L);
    }
}