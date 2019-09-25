package communication;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by hao on 10/27/18.
 */

public class TickectMaker {
    public ArrayList<Ticket> MakeCards(){
        ArrayList<Ticket> result = new ArrayList<Ticket>();
        try {
            Scanner sc = new Scanner(new File("DistinationTicketCardSourceInfo.txt"));
            while (sc.hasNextLine()) {
                String beginningCity = sc.nextLine();
                System.out.println(beginningCity);
                String endingCity = sc.nextLine();
                System.out.println(endingCity);
                int value = Integer.parseInt(sc.nextLine());
                Ticket d = new Ticket(new City(beginningCity, 0, 0), new City(endingCity, 0,0),value);
                System.out.println(value);
                result.add(d);
                if (sc.hasNextLine())
                sc.nextLine();
            }
        }
        catch ( Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
