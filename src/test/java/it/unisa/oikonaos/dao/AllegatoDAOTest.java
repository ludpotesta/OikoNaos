package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.dto.AllegatoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllegatoDAOTest {

    private AllegatoDAO allegatoDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        allegatoDAO = new AllegatoDAO();
    }

    @Test
    void testSalva_Success() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        allegatoDAO.salva(mockConnection, "foto.jpg", "/upload/foto.jpg", "image/jpeg", 100L);

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setString(1, "foto.jpg");
        verify(mockPreparedStatement).setLong(4, 100L);
    }

    @Test
    void testDoRetrieveByTicket_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo 1 allegato
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("NomeFile")).thenReturn("documento.pdf");
        when(mockResultSet.getString("PathFile")).thenReturn("/files/doc.pdf");
        when(mockResultSet.getString("TipoFile")).thenReturn("application/pdf");

        // ACT
        List<AllegatoDTO> result = allegatoDAO.doRetrieveByTicket(mockConnection, 100L);

        // ASSERT
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("documento.pdf", result.get(0).getNomeFile());
        assertEquals("application/pdf", result.get(0).getTipo());
    }

    @Test
    void testDoRetrieveByTicket_Empty() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        // ACT
        List<AllegatoDTO> result = allegatoDAO.doRetrieveByTicket(mockConnection, 100L);

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSalva_SpecialCharacters() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo un nome file che potrebbe essere un attacco o un errore
        String nomeStrano = "../../etc/passwd";
        String pathStrano = "C:\\Windows\\System32\\calc.exe";

        // ACT
        allegatoDAO.salva(mockConnection, nomeStrano, pathStrano, "unknown", 100L);

        // ASSERT
        // Verifichiamo che i dati arrivino integri al DB (grazie al PreparedStatement)
        verify(mockPreparedStatement).setString(1, nomeStrano);
        verify(mockPreparedStatement).setString(2, pathStrano);
        verify(mockPreparedStatement).executeUpdate();
    }
    @Test
    void testDoRetrieveByTicket_DbError() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Simuliamo un errore di connessione
        when(mockPreparedStatement.executeQuery()).thenThrow(new SQLException("Connection closed"));

        // ACT & ASSERT
        // Poiché stiamo testando il metodo INTERNO, ci aspettiamo che l'eccezione esploda.
        // Questo è corretto: il wrapper pubblico la catturerebbe, ma qui verifichiamo la logica SQL.
        assertThrows(SQLException.class, () -> {
            allegatoDAO.doRetrieveByTicket(mockConnection, 100L);
        });
    }
}