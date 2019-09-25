package communication;

/**
 * Created by urimaj on 11/9/18.
 */

public class PlayerOpponent extends Player implements IPlayer{

    private int ticketNum;
    private int cardNum;


    public PlayerOpponent(String playerName, int ticketNum, int trainNum, int cardNum) {
        super(playerName);
        this.ticketNum = ticketNum;
        this.cardNum = cardNum;
    }


    public void setCardNum(Integer cardNum) {
        this.cardNum = cardNum;
    }

    public void setTicketNum(Integer ticketNum) {
        this.ticketNum = ticketNum;
    }

    @Override
    public int getTicketNum() {
        return ticketNum;
    }

    @Override
    public int getCardNum() {
        return cardNum;
    }

    @Override
    public PlayerIdentity getIdentity() {
        return PlayerIdentity.OPPONENT;
    }

}
