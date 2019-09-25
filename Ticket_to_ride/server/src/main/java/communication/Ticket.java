package communication;

/**
 * Created by jalton on 10/24/18.
 */

public class Ticket {
    private City city1;
    private City city2;
    private int value;

    public Ticket(City city1, City city2, int value) {
        this.city1 = city1;
        this.city2 = city2;
        this.value = value;
    }

    public City getCity1() {
        return city1;
    }

    public City getCity2() {
        return city2;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int result = city1.hashCode();
        result = 31 * result + city2.hashCode();
        result = 31 * result + value;
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;

        Ticket ticket = (Ticket) o;

        if (getValue() != ticket.getValue()) return false;
        if (!getCity1().equals(ticket.getCity1())) return false;
        return getCity2().equals(ticket.getCity2());
    }

    public String toString() {
        return "Ticket{" +
                "city1=" + city1 +
                ", city2=" + city2 +
                ", value=" + value +
                '}';

    }
}
