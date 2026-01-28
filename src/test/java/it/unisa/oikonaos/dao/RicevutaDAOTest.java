package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Ricevuta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import util.database;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RicevutaDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPs;
    @Mock private ResultSet mockRs;

    private RicevutaDAO ricevutaDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ricevutaDAO = new RicevutaDAO();
    }

    @Test
    void testCreaRicevuta_Successo() throws Exception {
        long idPagamento = 10L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            // 1. Controllo duplicati: Restituisce FALSE (nessuna ricevuta esiste)
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            // 2. Esecuzione Insert
            when(mockPs.executeUpdate()).thenReturn(1);

            ricevutaDAO.creaRicevuta(idPagamento);

            // Verifica che abbia chiamato l'update (quindi l'INSERT)
            verify(mockPs).executeUpdate();
            verify(mockPs).setLong(eq(2), eq(idPagamento));
        }
    }

    @Test
    void testGetRicevutaByPagamento() throws Exception {
        long idPagamento = 10L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            // Troviamo la ricevuta
            when(mockRs.next()).thenReturn(true);

            when(mockRs.getLong("ID_Ricevuta")).thenReturn(500L);
            when(mockRs.getString("CodiceTransazione")).thenReturn("TX-123456");
            when(mockRs.getBigDecimal("ImportoPagato")).thenReturn(new BigDecimal("75.50"));
            when(mockRs.getTimestamp("DataEmissione")).thenReturn(Timestamp.valueOf("2026-02-20 12:00:00"));

            Optional<Ricevuta> result = ricevutaDAO.getRicevutaByPagamento(idPagamento);

            assertTrue(result.isPresent());
            assertEquals("TX-123456", result.get().getCodiceTransazione());
            assertEquals(new BigDecimal("75.50"), result.get().getImporto());
        }
    }
}