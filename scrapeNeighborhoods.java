import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class scrapeNeighborhoods {
    private String coreURL = "https://www.homes.com/neighborhood-search/boston-ma/?bb=uqimkn46qHi9q_hqD"; // URL
    private List<String> descriptions = new ArrayList<>();

    public ScrapeNeighborhoods(String url) {
        this.coreURL = url;
    }

    public Document connectWithJsoup(String url) throws IOException, InterruptedException {
        Connection.Response response = Jsoup.connect(url)
            .userAgent("Mozilla/5.0")
            .timeout(10000)
            .followRedirects(true)
            .execute();

        Thread.sleep(1000); // Simple delay to avoid rapid requests that might get blocked
        return response.parse();
    }

    public List<String> scrapeDescriptions() {
        try {
            Document doc = connectWithJsoup(coreURL);
            Elements elements = doc.select(".description-container .property-description"); // CSS selector for descriptions
            for (Element element : elements) {
                descriptions.add(element.text());
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error scraping descriptions: " + e.getMessage());
        }
        return descriptions;
    }

    public static void main(String[] args) {
        ScrapeNeighborhoods scraper = new ScrapeNeighborhoods("https://www.example.com/city-examples");
        List<String> descriptions = scraper.scrapeDescriptions();
        descriptions.forEach(System.out::println);
    }
}
