import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        RealtorsParser parser = new RealtorsParser(); // Create an instance of your parser class
        LinkedList<House> houses = parser.collectAllHouses(); // Use the method to collect all houses across pages

        // Print the number of houses collected and details of each house to verify the method's functionality
        System.out.println("Number of houses collected: " + houses.size());
        for (House house : houses) {
            System.out.println("Address: " + house.address);
            System.out.println("Link: " + house.houseURL);
            System.out.println("Beds: " + house.bed);
            System.out.println("Baths: " + house.bath);
            System.out.println("Square Feet: " + house.sqft);
            System.out.println("Market COMP: " + house.marketCompetition);
            System.out.println("Walk Score: " + house.walkScore);
            System.out.println("Transit Score: " + house.transitScore);
            System.out.println("Bike Score: " + house.bikeScore);


            System.out.println("-------------------------");
        }
    }
}
