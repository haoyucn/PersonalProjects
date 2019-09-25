package com.obfuscation.ttr_phase1b.gameViews;

import java.util.List;

import communication.Ticket;

/**
 * Created by jalton on 10/24/18.
 */

public interface ITicketView extends IView {

    void setTickets(List<Ticket> tickets);

    boolean isGameSetup();

}
