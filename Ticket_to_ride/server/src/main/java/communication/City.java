package communication;

import java.io.Serializable;

/**
 * Created by jalton on 10/24/18.
 */

public class City implements Serializable {
    private String name;
    private int lat;
    private int lng;

    public City(String name, int lat, int lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;

        City city = (City) o;

        return name.equals(city.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    public String getName() {
        return name;
    }

    public int getLat() {
        return lat;
    }

    public int getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return name;
    }
}
