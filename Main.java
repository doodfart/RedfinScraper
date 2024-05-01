import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Asking user for their preferences
        System.out.println("Enter minimum square footage:");
        int minSqft = scanner.nextInt();  // Read integer input for minimum square footage

        System.out.println("Enter minimum number of bedrooms:");
        int minBedrooms = scanner.nextInt();  // Read integer input for minimum bedrooms

        System.out.println("Enter maximum price:");
        int maxPrice = scanner.nextInt();  // Read integer input for maximum price

        scanner.close();


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
        
    // Filter houses based on user input
    private static LinkedList<House> filterHouses(LinkedList<House> houses, int minSqft, int minBedrooms, int maxPrice) {
        LinkedList<House> filteredHouses = new LinkedList<>();
        for (House house : houses) {
            if (house.sqft >= minSqft && house.bed >= minBedrooms && house.cost <= maxPrice) {
                filteredHouses.add(house);
            }
        }
        return filteredHouses;


    }
