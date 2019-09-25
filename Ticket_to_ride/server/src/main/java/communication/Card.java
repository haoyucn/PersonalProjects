package communication;

/**
 * Created by jalton on 10/24/18.
 */

public class Card {

    private GameColor color;

    public Card(GameColor color) {
        this.color = color;
    }

    public GameColor getColor() {
        return color;
    }

    public void setColor(GameColor color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;

        Card card = (Card) o;

        return card.color == this.color;
    }

    @Override
    public int hashCode() {
        return color.hashCode();
    }
}
