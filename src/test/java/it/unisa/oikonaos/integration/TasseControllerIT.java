package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.RicevutaController;
import it.unisa.oikonaos.controller.SpeseController;
import it.unisa.oikonaos.controller.SupervisoreTasseController;
import it.unisa.oikonaos.dao.PagamentoDAO;
import it.unisa.oikonaos.dao.RicevutaDAO;
import it.unisa.oikonaos.dao.TassaDAO;
import it.unisa.oikonaos.dao.UserDAO;
import it.unisa.oikonaos.model.Ricevuta;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;


import java.sql.Date;
import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.*;

public class TasseControllerIT {

    //IT_TAS_01
    @Test
    void creazioneTassa() throws Exception {

        System.out.println("[IT-TAS-01] Avvio test");

        SupervisoreTasseController controller =
                new SupervisoreTasseController();
        System.out.println("[IT-TAS-01] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-TAS-01] Request/Response/Session mockati");

        Utente supervisore = new Utente();
        supervisore.setIdUtente(1L);
        supervisore.setRuolo("SUPERVISORE");
        System.out.println("[IT-TAS-01] Utente supervisore creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(supervisore);
        System.out.println("[IT-TAS-01] Sessione configurata");

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");

        when(request.getParameter("trimestre")).thenReturn("2026-Q1");
        when(request.getParameter("tipo")).thenReturn("ORDINARIA");
        when(request.getParameter("destinatario")).thenReturn("TUTTI");
        when(request.getParameter("importo")).thenReturn("150.00");
        when(request.getParameter("scadenza")).thenReturn("2026-03-31");

        System.out.println("[IT-TAS-01] Parametri tassa impostati");

        TassaDAO tassaMock = mock(TassaDAO.class);
        UserDAO userMock = mock(UserDAO.class);
        System.out.println("[IT-TAS-01] DAO mockati");

        var tassaField =
                SupervisoreTasseController.class.getDeclaredField("tassaDAO");
        tassaField.setAccessible(true);
        tassaField.set(controller, tassaMock);

        var userField =
                SupervisoreTasseController.class.getDeclaredField("utenteDAO");
        userField.setAccessible(true);
        userField.set(controller, userMock);

        System.out.println("[IT-TAS-01] DAO iniettati");

        controller.service(request, response);
        System.out.println("[IT-TAS-01] Controller eseguito");

        verify(tassaMock).creaTassa(
                "2026-Q1",
                150.00,
                Date.valueOf("2026-03-31"),
                "ORDINARIA",
                null
        );
        System.out.println("[IT-TAS-01] Verifica creaTassa OK");

        verify(response).sendRedirect(
                "/OikoNaos_war_exploded/SupervisoreTasseController"
        );
        System.out.println("[IT-TAS-01] Verifica redirect OK");

        System.out.println("[IT-TAS-01] Test completato");
    }

    //IT_TAS_02
    @Test
    void avvioFlussoPagamento() throws Exception {

        System.out.println("[IT-TAS-02] Avvio test");

        SpeseController controller = new SpeseController();
        System.out.println("[IT-TAS-02] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-TAS-02] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(30L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-TAS-02] Utente creato id=30 ruolo=COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);
        System.out.println("[IT-TAS-02] Sessione configurata");

        when(request.getMethod()).thenReturn("POST");
        when(request.getContextPath()).thenReturn("/OikoNaos_war_exploded");
        when(request.getParameter("action")).thenReturn("startPay");
        when(request.getParameter("idTassa")).thenReturn("7");
        System.out.println("[IT-TAS-02] Parametri: action=startPay, idTassa=7");

        try (MockedConstruction<PagamentoDAO> mocked =
                     mockConstruction(PagamentoDAO.class,
                             (mock, context) -> {
                                 when(mock.creaPagamentoDaTassa(7L, 30L)).thenReturn(123L);
                             })) {

            System.out.println("[IT-TAS-02] MockConstruction PagamentoDAO attivo");

            controller.service(request, response);
            System.out.println("[IT-TAS-02] Controller eseguito");

            System.out.println("[IT-TAS-02] DAO costruiti: " + mocked.constructed());
            PagamentoDAO pagamentoMock = mocked.constructed().get(0);

            verify(pagamentoMock).creaPagamentoDaTassa(7L, 30L);
            System.out.println("[IT-TAS-02] Verifica creaPagamentoDaTassa OK");

            verify(response).sendRedirect(
                    "/OikoNaos_war_exploded/SpeseController?action=confirm&idPagamento=123"
            );
            System.out.println("[IT-TAS-02] Verifica redirect OK");
        }

        System.out.println("[IT-TAS-02] Test completato");
    }

    // IT-TAS-03
    @Test
    void visualizzazioneRicevuta() throws Exception {

        System.out.println("[IT-TAS-03] Avvio test");

        RicevutaController controller = new RicevutaController();
        System.out.println("[IT-TAS-03] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        System.out.println("[IT-TAS-03] Request/Response/Dispatcher mockati");

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("idPagamento")).thenReturn("100");
        when(request.getRequestDispatcher("/ricevuta.jsp"))
                .thenReturn(dispatcher);
        System.out.println("[IT-TAS-03] Parametri request impostati");

        Ricevuta ricevuta = new Ricevuta();
        ricevuta.setIdRicevuta(50L);
        ricevuta.setCodiceTransazione("TX-123456");
        System.out.println("[IT-TAS-03] Oggetto Ricevuta creato");

        try (MockedConstruction<RicevutaDAO> mocked =
                     mockConstruction(RicevutaDAO.class,
                             (mock, context) -> {
                                 when(mock.getRicevutaByPagamento(100L))
                                         .thenReturn(Optional.of(ricevuta));
                             })) {

            System.out.println("[IT-TAS-03] MockConstruction RicevutaDAO attivo");

            controller.service(request, response);
            System.out.println("[IT-TAS-03] Controller eseguito");

            System.out.println("[IT-TAS-03] DAO costruiti: " + mocked.constructed());
            RicevutaDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).getRicevutaByPagamento(100L);
            System.out.println("[IT-TAS-03] Verifica chiamata DAO OK");

            verify(request).setAttribute("ricevuta", ricevuta);
            System.out.println("[IT-TAS-03] Verifica attributo ricevuta OK");

            verify(dispatcher).forward(request, response);
            System.out.println("[IT-TAS-03] Verifica forward OK");
        }

        System.out.println("[IT-TAS-03] Test completato");
    }

    // IT-TAS-04
    @Test
    void dettaglioTasseSupervisore() throws Exception {

        System.out.println("[IT-TAS-04] Avvio test");

        SupervisoreTasseController controller =
                new SupervisoreTasseController();
        System.out.println("[IT-TAS-04] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        System.out.println("[IT-TAS-04] Request/Response/Session mockati");

        when(request.getMethod()).thenReturn("GET");

        Utente supervisore = new Utente();
        supervisore.setIdUtente(1L);
        supervisore.setRuolo("SUPERVISORE");
        System.out.println("[IT-TAS-04] Utente supervisore creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(supervisore);

        when(request.getRequestDispatcher("/supervisore/tasseSupervisore.jsp"))
                .thenReturn(dispatcher);
        System.out.println("[IT-TAS-04] Sessione e dispatcher configurati");

        TassaDAO tassaMock = mock(TassaDAO.class);
        UserDAO userMock = mock(UserDAO.class);

        when(tassaMock.doRetrieveAll()).thenReturn(List.of());
        when(userMock.doRetrieveCoinquiliniEscluso(1L)).thenReturn(List.of());

        System.out.println("[IT-TAS-04] DAO mockati");

        var tassaField =
                SupervisoreTasseController.class.getDeclaredField("tassaDAO");
        tassaField.setAccessible(true);
        tassaField.set(controller, tassaMock);

        var userField =
                SupervisoreTasseController.class.getDeclaredField("utenteDAO");
        userField.setAccessible(true);
        userField.set(controller, userMock);

        System.out.println("[IT-TAS-04] DAO iniettati");

        controller.service(request, response);
        System.out.println("[IT-TAS-04] Controller eseguito");

        verify(tassaMock).doRetrieveAll();
        verify(userMock).doRetrieveCoinquiliniEscluso(1L);
        System.out.println("[IT-TAS-04] Verifica chiamate DAO OK");

        verify(dispatcher).forward(request, response);
        System.out.println("[IT-TAS-04] Verifica forward OK");

        System.out.println("[IT-TAS-04] Test completato");
    }

}
