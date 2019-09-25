package com.obfuscation.ttr_phase1b.gameViews;

import java.util.List;

import communication.Ticket;

public interface IPTicketsView extends IView {

    void setTickets(List<Ticket> tickets);

}
