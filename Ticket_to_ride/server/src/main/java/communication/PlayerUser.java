package communication;

import java.util.ArrayList;

/**
 * Created by urimaj on 11/9/18.
 */

public class PlayerUser extends Player implements IPlayer{

    private ArrayList<Ticket> tickets;
    private ArrayList<Card> cards;
    private ArrayList<Ticket> ticketToChoose;
    private ArrayList<Card> cardToChoose;

    public PlayerUser() {
        tickets = new ArrayList<>();
        cards = new ArrayList<>();
        ticketToChoose = new ArrayList<>();
        cardToChoose = new ArrayList<>();
    }

    public PlayerUser(String playerName) {
        super(playerName);
        tickets = new ArrayList<>();
        cards = new ArrayList<>();
        ticketToChoose = new ArrayList<>();
        cardToChoose = new ArrayList<>();
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public void removeTicket(int i) {
        tickets.remove(i);
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public void addTickets(ArrayList<Ticket> moreTickets) {
        tickets.addAll(moreTickets);
//        System.out.println("Tickets number changed : " + tickets.size());
    }

    public void setTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
//        System.out.println("Tickets number changed : " + tickets.size());
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void removeCard(int i) {
        cards.remove(i);
    }

    public void removeFaceUpCardByIndex(int index) {
        cardToChoose.remove(index);
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCards(ArrayList<Card> moreCards) {
        cards.addAll(moreCards);
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public boolean useCards(GameColor color, int number) {
        int i, j = 0;
        for(i = 0; i < cards.size(); i++) {
            if(cards.get(i).getColor().equals(color)) {
                ++j;
            }
        }
        if(j >= number) {
            i = 0;
            while(i < cards.size() && number > 0) {
                if(cards.get(i).getColor().equals(color)) {
                    cards.remove(i);
                    --number;
                }else {
                    ++i;
                }
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlayerUser that = (PlayerUser) o;

        if (tickets != null ? !tickets.equals(that.tickets) : that.tickets != null) return false;
        if (cards != null ? !cards.equals(that.cards) : that.cards != null) return false;
        if (ticketToChoose != null ? !ticketToChoose.equals(that.ticketToChoose) : that.ticketToChoose != null)
            return false;
        return cardToChoose != null ? cardToChoose.equals(that.cardToChoose) : that.cardToChoose == null;
    }

    @Override
    public int hashCode() {
        int result = tickets != null ? tickets.hashCode() : 0;
        result = 31 * result + (cards != null ? cards.hashCode() : 0);
        result = 31 * result + (ticketToChoose != null ? ticketToChoose.hashCode() : 0);
        result = 31 * result + (cardToChoose != null ? cardToChoose.hashCode() : 0);
        return result;
    }

    @Override
    public int getCardNum() {
        return cards.size();
    }

    @Override
    public int getTicketNum() {
        return tickets.size();
    }

    public ArrayList<Ticket> getTicketToChoose() {
        return ticketToChoose;
    }

    public void setTicketToChoose(ArrayList<Ticket> ticketToChoose) {
        this.ticketToChoose = ticketToChoose;
    }

    public ArrayList<Card> getCardToChoose() {
        return cardToChoose;
    }

    public void setCardToChoose(ArrayList<Card> cardToChoose) {
        this.cardToChoose = cardToChoose;
    }

    @Override
    public PlayerIdentity getIdentity() {
        return PlayerIdentity.USER;
    }
}
