package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.RisorsaController;
import it.unisa.oikonaos.controller.SupervisoreRisorseController;
import it.unisa.oikonaos.dao.RichiestaRisorsaDAO;
import it.unisa.oikonaos.dao.RisorsaDAO;
import it.unisa.oikonaos.model.Utente;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.sql.Date;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class RisorseControllerIT {

    @Test
    void richiestaRisorsaValida() throws Exception {

        System.out.println("[IT-RIS-01] Avvio test");
        RisorsaController controller = new RisorsaController();
        System.out.println("[IT-RIS-01] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-RIS-01] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(30L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-RIS-01] Utente creato id=30 ruolo=COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        System.out.println("[IT-RIS-01] Sessione configurata");

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("idRisorsa")).thenReturn("5");

        LocalDate futura = LocalDate.now().plusDays(2);
        when(request.getParameter("data")).thenReturn(futura.toString());
        when(request.getParameter("accettaRegole")).thenReturn("on");
        System.out.println("[IT-RIS-01] Parametri: idRisorsa=5, data=" + futura + ", regole=on");

        try (MockedConstruction<RichiestaRisorsaDAO> mocked =
                     mockConstruction(RichiestaRisorsaDAO.class,
                             (mock, context) -> {
                                 when(mock.esisteConflitto(anyLong(), any(LocalDate.class)))
                                         .thenReturn(false);
                             })) {

            System.out.println("[IT-RIS-01] MockConstruction DAO attivo");

            controller.service(request, response);
            System.out.println("[IT-RIS-01] Controller eseguito");

            System.out.println("[IT-RIS-01] DAO costruiti: " + mocked.constructed());
            RichiestaRisorsaDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).esisteConflitto(5L, futura);
            System.out.println("[IT-RIS-01] Verifica esisteConflitto OK");

            verify(daoMock).creaRichiesta(
                    5L,
                    30L,
                    Date.valueOf(futura),
                    Date.valueOf(futura)
            );
            System.out.println("[IT-RIS-01] Verifica creaRichiesta OK");

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/RisorsaController?success=true"
            );
            System.out.println("[IT-RIS-01] Verifica redirect OK");
        }

        System.out.println("[IT-RIS-01] Test completato");
    }

    @Test
    void richiestaRisorsa_dataPassata_redirectErrore() throws Exception {

        System.out.println("[IT-RIS-02] Avvio test");
        RisorsaController controller = new RisorsaController();
        System.out.println("[IT-RIS-02] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-RIS-02] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(30L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-RIS-02] Utente creato id=30 ruolo=COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        System.out.println("[IT-RIS-02] Sessione configurata");

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("idRisorsa")).thenReturn("5");

        LocalDate ieri = LocalDate.now().minusDays(1);
        when(request.getParameter("data")).thenReturn(ieri.toString());
        when(request.getParameter("accettaRegole")).thenReturn("on");
        System.out.println("[IT-RIS-02] Parametri: idRisorsa=5, data=" + ieri + ", regole=on");

        try (MockedConstruction<RichiestaRisorsaDAO> mocked =
                     mockConstruction(RichiestaRisorsaDAO.class)) {

            System.out.println("[IT-RIS-02] MockConstruction DAO attivo");

            controller.service(request, response);
            System.out.println("[IT-RIS-02] Controller eseguito");

            System.out.println("[IT-RIS-02] DAO costruiti: " + mocked.constructed());
            if (!mocked.constructed().isEmpty()) {
                verifyNoInteractions(mocked.constructed().get(0));
                System.out.println("[IT-RIS-02] Verifica nessuna interazione DAO OK");
            } else {
                System.out.println("[IT-RIS-02] Nessun DAO costruito (flusso terminato prima)");
            }

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/RisorsaController?error=data_passata"
            );
            System.out.println("[IT-RIS-02] Verifica redirect OK");
        }

        System.out.println("[IT-RIS-02] Test completato");
    }

    @Test
    void richiestaRisorsa_conflitto_redirectNonDisponibile() throws Exception {

        System.out.println("[IT-RIS-03] Avvio test");
        RisorsaController controller = new RisorsaController();
        System.out.println("[IT-RIS-03] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-RIS-03] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(30L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-RIS-03] Utente creato id=30 ruolo=COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        System.out.println("[IT-RIS-03] Sessione configurata");

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("idRisorsa")).thenReturn("5");

        LocalDate futura = LocalDate.now().plusDays(3);
        when(request.getParameter("data")).thenReturn(futura.toString());
        when(request.getParameter("accettaRegole")).thenReturn("on");
        System.out.println("[IT-RIS-03] Parametri: idRisorsa=5, data=" + futura + ", regole=on");

        try (MockedConstruction<RichiestaRisorsaDAO> mocked =
                     mockConstruction(RichiestaRisorsaDAO.class,
                             (mock, context) -> {
                                 when(mock.esisteConflitto(5L, futura)).thenReturn(true);
                             })) {

            System.out.println("[IT-RIS-03] MockConstruction DAO attivo");

            controller.service(request, response);
            System.out.println("[IT-RIS-03] Controller eseguito");

            System.out.println("[IT-RIS-03] DAO costruiti: " + mocked.constructed());
            RichiestaRisorsaDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).esisteConflitto(5L, futura);
            System.out.println("[IT-RIS-03] Verifica esisteConflitto OK");

            verify(daoMock, never()).creaRichiesta(anyLong(), anyLong(), any(), any());
            System.out.println("[IT-RIS-03] Verifica creaRichiesta NON chiamato OK");

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/RisorsaController?error=disponibile"
            );
            System.out.println("[IT-RIS-03] Verifica redirect OK");
        }

        System.out.println("[IT-RIS-03] Test completato");
    }

    @Test
    void richiestaRisorsa_regoleNonAccettate_redirectErroreRegole() throws Exception {

        System.out.println("[IT-RIS-04] Avvio test");
        RisorsaController controller = new RisorsaController();
        System.out.println("[IT-RIS-04] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-RIS-04] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(30L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-RIS-04] Utente creato id=30 ruolo=COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        System.out.println("[IT-RIS-04] Sessione configurata");

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("idRisorsa")).thenReturn("5");

        LocalDate futura = LocalDate.now().plusDays(2);
        when(request.getParameter("data")).thenReturn(futura.toString());
        when(request.getParameter("accettaRegole")).thenReturn(null);
        System.out.println("[IT-RIS-04] Parametri: idRisorsa=5, data=" + futura + ", regole=null");

        try (MockedConstruction<RichiestaRisorsaDAO> mocked =
                     mockConstruction(RichiestaRisorsaDAO.class)) {

            System.out.println("[IT-RIS-04] MockConstruction DAO attivo");

            controller.service(request, response);
            System.out.println("[IT-RIS-04] Controller eseguito");

            System.out.println("[IT-RIS-04] DAO costruiti: " + mocked.constructed());
            if (!mocked.constructed().isEmpty()) {
                verifyNoInteractions(mocked.constructed().get(0));
                System.out.println("[IT-RIS-04] Verifica nessuna interazione DAO OK");
            } else {
                System.out.println("[IT-RIS-04] Nessun DAO costruito (flusso terminato prima)");
            }

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/RisorsaController?error=regole"
            );
            System.out.println("[IT-RIS-04] Verifica redirect OK");
        }

        System.out.println("[IT-RIS-04] Test completato");
    }

    @Test
    void supervisoreAccettaRichiesta() throws Exception {

        System.out.println("[IT-RIS-05] Avvio test");

        SupervisoreRisorseController controller =
                new SupervisoreRisorseController();
        System.out.println("[IT-RIS-05] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        System.out.println("[IT-RIS-05] Request/Response mockate");

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("accettaRichiesta");
        when(request.getParameter("idRichiesta")).thenReturn("10");
        System.out.println("[IT-RIS-05] Parametri: action=accettaRichiesta, idRichiesta=10");

        RichiestaRisorsaDAO richiestaMock = mock(RichiestaRisorsaDAO.class);
        RisorsaDAO risorsaMock = mock(RisorsaDAO.class);
        System.out.println("[IT-RIS-05] DAO mockati");

        var richiestaField =
                SupervisoreRisorseController.class.getDeclaredField("richiestaDAO");
        richiestaField.setAccessible(true);
        richiestaField.set(controller, richiestaMock);
        System.out.println("[IT-RIS-05] richiestaDAO iniettato");

        var risorsaField =
                SupervisoreRisorseController.class.getDeclaredField("risorsaDAO");
        risorsaField.setAccessible(true);
        risorsaField.set(controller, risorsaMock);
        System.out.println("[IT-RIS-05] risorsaDAO iniettato");

        controller.service(request, response);
        System.out.println("[IT-RIS-05] Controller eseguito");

        verify(richiestaMock).aggiornaStato(10L, "APPROVATA");
        System.out.println("[IT-RIS-05] Verifica aggiornaStato OK");

        verify(response).sendRedirect("SupervisoreRisorseController");
        System.out.println("[IT-RIS-05] Verifica redirect OK");

        System.out.println("[IT-RIS-05] Test completato");
    }

    @Test
    void supervisoreRifiutaRichiesta() throws Exception {

        System.out.println("[IT-RIS-06] Avvio test");

        SupervisoreRisorseController controller =
                new SupervisoreRisorseController();
        System.out.println("[IT-RIS-06] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        System.out.println("[IT-RIS-06] Request/Response mockate");

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("rifiutaRichiesta");
        when(request.getParameter("idRichiesta")).thenReturn("10");
        System.out.println("[IT-RIS-06] Parametri: action=rifiutaRichiesta, idRichiesta=10");

        RichiestaRisorsaDAO richiestaMock = mock(RichiestaRisorsaDAO.class);
        RisorsaDAO risorsaMock = mock(RisorsaDAO.class);
        System.out.println("[IT-RIS-06] DAO mockati");

        var richiestaField =
                SupervisoreRisorseController.class.getDeclaredField("richiestaDAO");
        richiestaField.setAccessible(true);
        richiestaField.set(controller, richiestaMock);
        System.out.println("[IT-RIS-06] richiestaDAO iniettato");

        var risorsaField =
                SupervisoreRisorseController.class.getDeclaredField("risorsaDAO");
        risorsaField.setAccessible(true);
        risorsaField.set(controller, risorsaMock);
        System.out.println("[IT-RIS-06] risorsaDAO iniettato");

        controller.service(request, response);
        System.out.println("[IT-RIS-06] Controller eseguito");

        verify(richiestaMock).aggiornaStato(10L, "RIFIUTATA");
        System.out.println("[IT-RIS-06] Verifica aggiornaStato OK");

        verify(response).sendRedirect("SupervisoreRisorseController");
        System.out.println("[IT-RIS-06] Verifica redirect OK");

        System.out.println("[IT-RIS-06] Test completato");
    }
}
