package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.Pagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import util.database;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PagamentoDAOTest {

    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPs;
    @Mock private ResultSet mockRs;

    private PagamentoDAO pagamentoDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pagamentoDAO = new PagamentoDAO();
    }

    @Test
    void testGetPagamentiByUtente() throws Exception {
        long idUtente = 1L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            // Setup connessione
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            // Simuliamo 1 riga di risultato
            when(mockRs.next()).thenReturn(true).thenReturn(false);

            // Dati simulati dal DB
            when(mockRs.getLong("ID_Pagamento")).thenReturn(10L);
            when(mockRs.getLong("ID_Tassa")).thenReturn(5L);
            when(mockRs.getBigDecimal("ImportoPagato")).thenReturn(new BigDecimal("100.00"));
            when(mockRs.getString("MetodoPagamento")).thenReturn("Carta");
            when(mockRs.getString("TrimestreRiferimento")).thenReturn("Q1 2026");
            when(mockRs.getDate("Scadenza")).thenReturn(Date.valueOf("2026-03-31"));
            when(mockRs.getTimestamp("DataPagamento")).thenReturn(Timestamp.valueOf("2026-02-15 10:00:00"));

            // Esecuzione
            List<Pagamento> risultati = pagamentoDAO.getPagamentiByUtente(idUtente);

            // Verifica
            assertNotNull(risultati);
            assertEquals(1, risultati.size());
            assertEquals(10L, risultati.get(0).getIdPagamento());
            assertEquals("Q1 2026", risultati.get(0).getPeriodo());
        }
    }

    @Test
    void testGetPagamentoById() throws Exception {
        long idPagamento = 10L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockPs.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true); // Trova il pagamento

            when(mockRs.getLong("ID_Pagamento")).thenReturn(idPagamento);
            when(mockRs.getBigDecimal("ImportoPagato")).thenReturn(new BigDecimal("50.00"));
            when(mockRs.getString("TrimestreRiferimento")).thenReturn("Q2 2026");
            when(mockRs.getDate("Scadenza")).thenReturn(Date.valueOf("2026-06-30"));
            when(mockRs.getTimestamp("DataPagamento")).thenReturn(null); // Non ancora pagato

            Pagamento p = pagamentoDAO.getPagamentoById(idPagamento);

            assertNotNull(p);
            assertEquals(idPagamento, p.getIdPagamento());
            assertEquals(new BigDecimal("50.00"), p.getImportoPagato());
        }
    }

    @Test
    void testCreaPagamentoDaTassa_NuovoInserimento() throws Exception {
        // Testiamo il caso in cui il pagamento NON esiste e viene creato
        long idTassa = 5L;
        long idUtente = 1L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);

            // Il metodo fa due query: SELECT (check) e INSERT
            // Dobbiamo gestire due PreparedStatement o usare lo stesso mock per entrambi
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);
            when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);

            // 1. La select di controllo restituisce FALSE (nessun pagamento esiste)
            when(mockPs.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            // 2. L'insert restituisce 1 (riga inserita)
            when(mockPs.executeUpdate()).thenReturn(1);

            // 3. Recupero chiave generata (ID del nuovo pagamento)
            ResultSet mockKeys = mock(ResultSet.class);
            when(mockPs.getGeneratedKeys()).thenReturn(mockKeys);
            when(mockKeys.next()).thenReturn(true);
            when(mockKeys.getLong(1)).thenReturn(99L); // ID generato finto

            long idGenerato = pagamentoDAO.creaPagamentoDaTassa(idTassa, idUtente);

            assertEquals(99L, idGenerato);

            // Verifica che abbia provato a fare l'insert
            verify(mockPs).executeUpdate();
        }
    }
}