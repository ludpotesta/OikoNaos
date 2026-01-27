package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Risorsa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RisorsaDAOTest {

    private RisorsaDAO risorsaDAO;

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPreparedStatement;
    @Mock private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        risorsaDAO = new RisorsaDAO();
    }

    @Test
    void testDoSave_FullData() throws Exception {
        // ARRANGE
        Risorsa r = new Risorsa();
        r.setNome("Proiettore");
        r.setDescrizione("Proiettore HD");
        r.setRegoleUso("Spegnere dopo l'uso");
        r.setPenale(new BigDecimal("50.00")); // Penale presente

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        risorsaDAO.doSave(mockConnection, r);

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();
        verify(mockPreparedStatement).setString(1, "Proiettore");
        verify(mockPreparedStatement).setBigDecimal(4, new BigDecimal("50.00"));
    }

    @Test
    void testDoSave_WithNullPenale() throws Exception {
        // ARRANGE
        Risorsa r = new Risorsa();
        r.setNome("Libro");
        r.setDescrizione("Libro condiviso");
        r.setRegoleUso("Non scrivere sopra");
        r.setPenale(null); // NESSUNA PENALE

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // ACT
        risorsaDAO.doSave(mockConnection, r);

        // ASSERT
        verify(mockPreparedStatement).executeUpdate();

        // Verifica fondamentale: deve aver chiamato setNull sul parametro 4
        verify(mockPreparedStatement).setNull(4, Types.DECIMAL);
    }

    @Test
    void testDoRetrieveAll_Found() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        // Simuliamo 1 risorsa trovata
        when(mockResultSet.next()).thenReturn(true, false);

        when(mockResultSet.getLong("ID_Risorsa")).thenReturn(1L);
        when(mockResultSet.getString("Nome")).thenReturn("Bici");
        when(mockResultSet.getString("Descrizione")).thenReturn("Mountain Bike");
        when(mockResultSet.getString("RegoleUso")).thenReturn("Riportare in garage");
        when(mockResultSet.getBigDecimal("Penale")).thenReturn(new BigDecimal("100.00"));

        // ACT
        List<Risorsa> lista = risorsaDAO.doRetrieveAll(mockConnection);

        // ASSERT
        assertNotNull(lista);
        assertEquals(1, lista.size());
        assertEquals("Bici", lista.get(0).getNome());
        assertEquals(new BigDecimal("100.00"), lista.get(0).getPenale());
    }

    @Test
    void testDoRetrieveAll_Empty() throws Exception {
        // ARRANGE
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Nessun risultato

        // ACT
        List<Risorsa> lista = risorsaDAO.doRetrieveAll(mockConnection);

        // ASSERT
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }
}