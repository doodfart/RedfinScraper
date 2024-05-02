import java.util.LinkedList;
import java.util.Scanner;

public class main {

    public static void main (String [] args) {
        String baseUrl;

        System.out.println("""
                Welcome to RedfinScraper. The goal of this application
                is to be a one stop shop for identifying ideal houses and trip locations in
                the neighborhood which best match your preferences. To begin, choose one
                of the following 10 cities to explore
                1. Philadelphia
                2. NYC
                3. Chicago
                4. Los Angeles
                5. Washington D.C""");

        System.out.println("Enter just a number (1-5) of the city you'd like to explore:");
        Scanner scanner = new Scanner(System.in);
        int city = scanner.nextInt();
        System.out.println("Ah, nice choice!");

        baseUrl = switch (city) {
            case 2 -> "https://www.redfin.com/city/30749/NY/New-York";
            case 3 -> "https://www.redfin.com/city/29470/IL/Chicago";
            case 4 -> "https://www.redfin.com/city/11203/CA/Los-Angeles";
            case 5 -> "https://www.redfin.com/city/12839/DC/Washington-DC";
            default -> "https://www.redfin.com/city/15502/PA/Philadelphia";
        };

        realtorsParser z = new realtorsParser(baseUrl);
        LinkedList<house> Redfinhouses = z.collectAllHouses();


        System.out.println("For reference here are the summary statistics of the city you chose");
        realtorsParser.calculateStatistics(Redfinhouses);

        System.out.println("""
                \n There are now a couple of options to explore
                Would you like to identify a specific house which most closely
                matches your budget and needs or would you instead like to
                give a couple of characteristics of your ideal neighborhood
                in the region you identified above and we'll tell you which
                neighborhood most closely matches your preferences
                """);

        boolean stay = true;
        while(stay) {
            System.out.println("1. Identify your one ideal house\n" +
                    "2. Explore neighborhoods which match your preferences\n" +
                    "3. Exit");
            Scanner option = new Scanner(System.in);
            System.out.println("Pick a number (1-3)");
            int op = scanner.nextInt();

            switch (op) {
                case 1 -> z.closestHouseMatch();
                case 2 -> System.out.println("Option 2");
                default -> stay = false;
            };
        }
        z.printOutAll();
    }
}
