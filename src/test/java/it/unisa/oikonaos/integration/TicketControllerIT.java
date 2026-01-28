package it.unisa.oikonaos.integration;

import it.unisa.oikonaos.controller.SupervisoreTicketController;
import it.unisa.oikonaos.controller.TicketController;
import it.unisa.oikonaos.dao.AggiornamentoTicketDAO;
import it.unisa.oikonaos.dao.TicketDAO;
import it.unisa.oikonaos.model.Utente;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.mockito.Mockito.*;

class TicketControllerIT {

    // IT-TICK-01
    @Test
    void creazioneTicketValida() throws Exception {

        System.out.println("[IT-TICK-01] Avvio test");

        TicketController controller = new TicketController();
        System.out.println("[IT-TICK-01] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-TICK-01] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(20L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-TICK-01] Utente coinquilino creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");

        when(request.getParameter("titolo")).thenReturn("Perdita acqua");
        when(request.getParameter("descrizione")).thenReturn("Perdita sotto il lavello");
        when(request.getParameter("categoria")).thenReturn("SPAZIO_COMUNE");
        when(request.getParameter("priorita")).thenReturn("ALTA");

        when(request.getParts()).thenReturn(java.util.Collections.emptyList());
        System.out.println("[IT-TICK-01] Parametri request impostati");

        try (MockedConstruction<TicketDAO> mocked =
                     mockConstruction(TicketDAO.class,
                             (mock, context) -> {
                                 when(mock.creaTicket(
                                         anyString(),
                                         anyString(),
                                         anyString(),
                                         anyString(),
                                         anyLong()
                                 )).thenReturn(100L);
                             })) {

            System.out.println("[IT-TICK-01] TicketDAO intercettato");

            controller.service(request, response);
            System.out.println("[IT-TICK-01] Controller eseguito");

            TicketDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).creaTicket(
                    "Perdita acqua",
                    "Perdita sotto il lavello",
                    "SPAZIO_COMUNE",
                    "ALTA",
                    20L
            );
            System.out.println("[IT-TICK-01] Creazione ticket verificata");

            verify(response).sendRedirect("TicketController");
            System.out.println("[IT-TICK-01] Redirect finale OK");
        }

        System.out.println("[IT-TICK-01] Test completato");
    }

    // IT-TICK-02
    @Test
    void creazioneTicketErrore_redirectErroreGenerico() throws Exception {

        System.out.println("[IT-TICK-02] Avvio test");

        TicketController controller = new TicketController();
        System.out.println("[IT-TICK-02] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-TICK-02] Request/Response/Session mockati");

        Utente u = new Utente();
        u.setIdUtente(20L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-TICK-02] Utente coinquilino creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");

        when(request.getParameter("titolo")).thenReturn("Perdita acqua");
        when(request.getParameter("descrizione")).thenReturn("Descrizione");
        when(request.getParameter("categoria")).thenReturn("IDRAULICA");
        when(request.getParameter("priorita")).thenReturn("ALTA");

        when(request.getParts()).thenReturn(java.util.Collections.emptyList());
        System.out.println("[IT-TICK-02] Parametri request impostati");

        try (MockedConstruction<TicketDAO> mocked =
                     mockConstruction(TicketDAO.class,
                             (mock, context) -> {
                                 when(mock.creaTicket(
                                         anyString(),
                                         anyString(),
                                         anyString(),
                                         anyString(),
                                         anyLong()
                                 )).thenThrow(new RuntimeException("DB error"));
                             })) {

            System.out.println("[IT-TICK-02] TicketDAO intercettato");

            controller.service(request, response);
            System.out.println("[IT-TICK-02] Controller eseguito");

            TicketDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).creaTicket(
                    "Perdita acqua",
                    "Descrizione",
                    "IDRAULICA",
                    "ALTA",
                    20L
            );
            System.out.println("[IT-TICK-02] Eccezione DB simulata correttamente");

            verify(response).sendRedirect("nuovoTicket.jsp?error=generic");
            System.out.println("[IT-TICK-02] Redirect errore OK");
        }

        System.out.println("[IT-TICK-02] Test completato");
    }

    // IT-TICK-03
    @Test
    void dettagliTicket_nonAutore_redirect() throws Exception {

        System.out.println("[IT-TICK-03] Avvio test");

        TicketController controller = new TicketController();
        System.out.println("[IT-TICK-03] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        System.out.println("[IT-TICK-03] Request/Response/Session/Dispatcher mockati");

        Utente u = new Utente();
        u.setIdUtente(20L);
        u.setRuolo("COINQUILINO");
        System.out.println("[IT-TICK-03] Utente coinquilino creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("action")).thenReturn("details");
        when(request.getParameter("idTicket")).thenReturn("100");

        it.unisa.oikonaos.model.Ticket ticket =
                new it.unisa.oikonaos.model.Ticket();
        ticket.setIdTicket(100L);
        ticket.setIdAutore(999L);
        System.out.println("[IT-TICK-03] Ticket non appartenente all'utente");

        try (MockedConstruction<TicketDAO> mocked =
                     mockConstruction(TicketDAO.class,
                             (mock, context) -> {
                                 when(mock.doRetrieveById(100L))
                                         .thenReturn(ticket);
                             })) {

            controller.service(request, response);
            System.out.println("[IT-TICK-03] Controller eseguito");

            TicketDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).doRetrieveById(100L);
            verify(response).sendRedirect("TicketController");
            verify(dispatcher, never()).forward(any(), any());
            verify(request, never()).setAttribute(eq("ticket"), any());

            System.out.println("[IT-TICK-03] Redirect per accesso non autorizzato OK");
        }

        System.out.println("[IT-TICK-03] Test completato");
    }

    // IT-TICK-04
    @Test
    void aggiornamentoStatoTicket_supervisore() throws Exception {

        System.out.println("[IT-TICK-04] Avvio test");

        SupervisoreTicketController controller =
                new SupervisoreTicketController();
        System.out.println("[IT-TICK-04] Controller istanziato");

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        System.out.println("[IT-TICK-04] Request/Response/Session mockati");

        Utente sup = new Utente();
        sup.setIdUtente(1L);
        sup.setRuolo("SUPERVISORE");
        System.out.println("[IT-TICK-04] Utente supervisore creato");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(sup);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("updateStato");
        when(request.getParameter("idTicket")).thenReturn("100");
        when(request.getParameter("nuovoStato")).thenReturn("IN_LAVORAZIONE");

        try (
                MockedConstruction<TicketDAO> ticketMocked =
                        mockConstruction(TicketDAO.class);
                MockedConstruction<AggiornamentoTicketDAO> storicoMocked =
                        mockConstruction(AggiornamentoTicketDAO.class)
        ) {

            System.out.println("[IT-TICK-04] DAO intercettati");

            controller.service(request, response);
            System.out.println("[IT-TICK-04] Controller eseguito");

            TicketDAO ticketDAO = ticketMocked.constructed().get(0);
            AggiornamentoTicketDAO storicoDAO =
                    storicoMocked.constructed().get(0);

            verify(ticketDAO).updateStato(100L, "IN_LAVORAZIONE");
            System.out.println("[IT-TICK-04] Aggiornamento stato verificato");

            verify(storicoDAO).creaAggiornamento(
                    100L,
                    1L,
                    "Stato aggiornato a: IN LAVORAZIONE"
            );
            System.out.println("[IT-TICK-04] Storico aggiornamento verificato");

            verify(response).sendRedirect(
                    "SupervisoreTicketController?msg=updated"
            );
            System.out.println("[IT-TICK-04] Redirect finale OK");
        }

        System.out.println("[IT-TICK-04] Test completato");
    }
}
