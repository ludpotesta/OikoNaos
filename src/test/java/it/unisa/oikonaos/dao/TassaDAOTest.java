package it.unisa.oikonaos.dao;

import it.unisa.oikonaos.model.TassaTrimestrale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import util.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TassaDAOTest {

    // 1. Dichiariamo i "finti" componenti del DB
    @Mock private Connection mockConnection;
    @Mock private PreparedStatement mockPs;
    @Mock private ResultSet mockRs;

    private TassaDAO tassaDAO;

    @BeforeEach
    void setUp() {
        // Inizializza i mock (@Mock) prima di ogni test
        MockitoAnnotations.openMocks(this);
        tassaDAO = new TassaDAO();
    }

    @Test
    void testGetTasseByUtente_Successo() throws Exception {
        // ID Utente di prova
        long idUtente = 1L;

        // --- FASE 1: ISTRUISCI IL MOCK (La Recita) ---

        // Questo blocco try serve a "congelare" la classe statica database per evitare che cerchi Tomcat
        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {

            // Quando il DAO chiede la connessione, dagli quella finta
            mockedDb.when(database::getConnection).thenReturn(mockConnection);

            // Quando la connessione prepara lo statement, dagli quello finto
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            // Quando lo statement esegue la query, dagli il ResultSet finto
            when(mockPs.executeQuery()).thenReturn(mockRs);

            // Simuliamo che il ResultSet trovi 1 riga (next = true, poi false)
            when(mockRs.next()).thenReturn(true).thenReturn(false);

            // Riempiamo i dati finti che il ResultSet deve restituire
            when(mockRs.getLong("ID_Tassa")).thenReturn(100L);
            when(mockRs.getString("TrimestreRiferimento")).thenReturn("Gen-Mar 2026");
            when(mockRs.getBigDecimal("ImportoDovuto")).thenReturn(new BigDecimal("150.00"));
            when(mockRs.getDate("Scadenza")).thenReturn(Date.valueOf("2026-03-30"));
            when(mockRs.getString("Stato")).thenReturn("DA_PAGARE");
            when(mockRs.getObject("ID_Pagamento", Long.class)).thenReturn(null);
            when(mockRs.getObject("ID_Ricevuta")).thenReturn(null);

            // --- FASE 2: AZIONE (Chiama il metodo vero) ---
            List<TassaTrimestrale> risultato = tassaDAO.getTasseByUtente(idUtente);

            // --- FASE 3: VERIFICA (Controlla se ha funzionato) ---
            assertNotNull(risultato);
            assertEquals(1, risultato.size(), "Dovrebbe esserci 1 tassa");

            TassaTrimestrale t = risultato.get(0);
            assertEquals(100L, t.getIdTassa());
            assertEquals("Gen-Mar 2026", t.getTrimestreRiferimento());
            assertEquals(LocalDate.of(2026, 3, 30), t.getScadenza());

            // Verifica che il DAO abbia settato i parametri corretti nella query SQL
            verify(mockPs, times(2)).setLong(anyInt(), eq(idUtente));
        }
    }

    @Test
    void testCreaTassa_Successo() throws Exception {
        // Dati di input
        String trimestre = "Apr-Giu 2026";
        double importo = 200.50;
        Date scadenza = Date.valueOf("2026-06-30");
        String tipo = "ORDINARIA";
        Long idUtente = 5L;

        // --- ISTRUISCI IL MOCK ---
        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {

            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            // Quando fa executeUpdate, fingiamo che restituisca 1 (1 riga inserita)
            when(mockPs.executeUpdate()).thenReturn(1);

            // --- AZIONE ---
            tassaDAO.creaTassa(trimestre, importo, scadenza, tipo, idUtente);

            // --- VERIFICA ---
            // Verifichiamo che abbia passato i dati giusti al PreparedStatement
            verify(mockPs).setString(1, trimestre);
            verify(mockPs).setDouble(2, importo);
            verify(mockPs).setDate(3, scadenza);
            verify(mockPs).setLong(5, idUtente);

            // Verifichiamo che abbia effettivamente eseguito l'inserimento
            verify(mockPs).executeUpdate();
        }
    }

    @Test
    void testMarcaComePagata() throws Exception {
        long idTassa = 50L;

        try (MockedStatic<database> mockedDb = Mockito.mockStatic(database.class)) {
            mockedDb.when(database::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPs);

            tassaDAO.marcaComePagata(idTassa);

            verify(mockPs).setLong(1, idTassa);
            verify(mockPs).executeUpdate();
        }
    }
}