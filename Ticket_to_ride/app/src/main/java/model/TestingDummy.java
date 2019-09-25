package model;

import java.util.ArrayList;

import communication.TickectMaker;
import communication.Ticket;

/**
 * Created by hao on 10/27/18.
 */

public class TestingDummy {

    public static void main(String[] argv) {
        TickectMaker cardMaker = new TickectMaker();

        ArrayList<Ticket> cardDeck = cardMaker.MakeCards();

        System.out.println(cardDeck.size());
    }
}
