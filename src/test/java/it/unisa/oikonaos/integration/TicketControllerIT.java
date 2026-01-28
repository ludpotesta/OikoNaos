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
        TicketController controller = new TicketController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(20L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");

        when(request.getParameter("titolo")).thenReturn("Perdita acqua");
        when(request.getParameter("descrizione")).thenReturn("Perdita sotto il lavello");
        when(request.getParameter("categoria")).thenReturn("SPAZIO_COMUNE");
        when(request.getParameter("priorita")).thenReturn("ALTA");

        //niente allegati
        when(request.getParts()).thenReturn(java.util.Collections.emptyList());

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

            controller.service(request, response);
            TicketDAO daoMock = mocked.constructed().get(0);

            verify(daoMock).creaTicket(
                    "Perdita acqua",
                    "Perdita sotto il lavello",
                    "SPAZIO_COMUNE",
                    "ALTA",
                    20L
            );

            verify(response).sendRedirect("TicketController");
        }
    }

    // IT-TICK-02
    @Test
    void creazioneTicketErrore_redirectErroreGenerico() throws Exception {

        TicketController controller = new TicketController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        Utente u = new Utente();
        u.setIdUtente(20L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("POST");
        when(request.getParameter("action")).thenReturn("create");

        when(request.getParameter("titolo")).thenReturn("Perdita acqua");
        when(request.getParameter("descrizione")).thenReturn("Descrizione");
        when(request.getParameter("categoria")).thenReturn("IDRAULICA");
        when(request.getParameter("priorita")).thenReturn("ALTA");

        // Nessun allegato
        when(request.getParts()).thenReturn(java.util.Collections.emptyList());

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

            controller.service(request, response);

            TicketDAO daoMock = mocked.constructed().get(0);
            verify(daoMock).creaTicket(
                    "Perdita acqua",
                    "Descrizione",
                    "IDRAULICA",
                    "ALTA",
                    20L
            );

            System.out.println("DAO mockati: " + mocked.constructed());
            verify(response).sendRedirect("nuovoTicket.jsp?error=generic");
        }
    }

    // IT-TICK-03
    @Test
    void dettagliTicket_nonAutore_redirect() throws Exception {

        TicketController controller = new TicketController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        Utente u = new Utente();
        u.setIdUtente(20L);
        u.setRuolo("COINQUILINO");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("utente")).thenReturn(u);

        when(request.getMethod()).thenReturn("GET");
        when(request.getParameter("action")).thenReturn("details");
        when(request.getParameter("idTicket")).thenReturn("100");

        // Ticket NON dell'utente
        it.unisa.oikonaos.model.Ticket ticket = new it.unisa.oikonaos.model.Ticket();
        ticket.setIdTicket(100L);
        ticket.setIdAutore(999L); // autore diverso

        try (MockedConstruction<TicketDAO> mocked =
                     mockConstruction(TicketDAO.class,
                             (mock, context) -> {
                                 when(mock.doRetrieveById(100L))
                                         .thenReturn(ticket);
                             })) {

            controller.service(request, response);

            TicketDAO daoMock = mocked.constructed().get(0);
            verify(daoMock).doRetrieveById(100L);
            verify(response).sendRedirect("TicketController");
            verify(dispatcher, never()).forward(any(), any());
            verify(request, never()).setAttribute(eq("ticket"), any());
        }
    }

    // IT-TICK-04
    @Test
    void aggiornamentoStatoTicket_supervisore() throws Exception {

        SupervisoreTicketController controller =
                new SupervisoreTicketController();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        // Supervisore autenticato
        Utente sup = new Utente();
        sup.setIdUtente(1L);
        sup.setRuolo("SUPERVISORE");

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

            controller.service(request, response);

            TicketDAO ticketDAO = ticketMocked.constructed().get(0);
            AggiornamentoTicketDAO storicoDAO =
                    storicoMocked.constructed().get(0);

            //aggiornamento stato
            verify(ticketDAO).updateStato(100L, "IN_LAVORAZIONE");

            //salvataggio storico
            verify(storicoDAO).creaAggiornamento(
                    100L,
                    1L,
                    "Stato aggiornato a: IN LAVORAZIONE"
            );

            //redirect finale
            verify(response).sendRedirect(
                    "SupervisoreTicketController?msg=updated"
            );
        }
    }

}

