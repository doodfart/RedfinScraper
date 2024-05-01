import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        RealtorsParser z = new RealtorsParser();
        try {
            // Fetching the first page of listings as a sample
            Document samplePage = Jsoup.connect("https://www.redfin.com/city/15502/PA/Philadelphia").get();

            // Extracting house links from the sample page
            List<String> houseLinks = z.extractHouseLinks(samplePage);

            // Print the size of the list and each link to verify the method's functionality
            System.out.println("Number of house links extracted: " + houseLinks.size());
            for (String link : houseLinks) {
                System.out.println(link);
            }
        } catch (IOException e) {
            System.out.println("Error fetching the page: " + e.getMessage());
        }
    }
}
