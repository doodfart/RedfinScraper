import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class RealtorsParser {
    private String coreURL = "https://www.redfin.com/city/15502/PA/Philadelphia";
    // https://www.redfin.com/city/15502/PA/Philadelphia
    private LinkedList<House> redfinHouses = new LinkedList<House>();  // List to store the collected house data
    private Map<String, String> cookies = new HashMap<>();

    public RealtorsParser() {
    }

    public Document connectWithJsoup(String url) {
        Document doc = null;
        try {
            Connection.Response response = Jsoup.connect(url)
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36")
                    .referrer("http://www.google.com")
                    .cookies(cookies)
                    .method(Connection.Method.GET)
                    .execute();

            cookies.putAll(response.cookies()); // Update the saved cookies

            doc = response.parse();
            // Random delay to mimic human browsing
            long sleepTime = 5000 + (long) (Math.random() * 10000);
            System.out.println("Sleeping for " + sleepTime + " milliseconds.");
            Thread.sleep(sleepTime);
        } catch (IOException | InterruptedException e) {
            System.out.println("Error connecting to the URL: " + e.getMessage());
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
        }
        return doc;
    }

    // Main method to collect all houses
    public LinkedList<House> collectAllHouses() {
        LinkedList<House> redfinHouses = new LinkedList<>();
        String baseURL = coreURL;
        for (int i = 1; i <= 2; i++) {
            try {
                Thread.sleep(10000);  // Delay to prevent being blocked by the server
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread interrupted: " + e);
            }

            String pageUrl = baseURL + "/page-" + i;
            System.out.println("Fetching URL: " + pageUrl); // Print the URL being accessed
            Document pageDoc = connectWithJsoup(pageUrl);
            if (pageDoc != null) {
                List<String> houseLinks = extractHouseLinks(pageDoc);
                System.out.println("Found " + houseLinks.size() + " house links on page " + i); // Debug the number of links found
                for (String link : houseLinks) {
                    System.out.println("Processing link: " + link); // Debug output
                    List<House> houses = getHouseFromPage(link);
                    redfinHouses.addAll(houses);
                }
            }
        }
        return redfinHouses;
    }


    // Extract house links from a single page
    public List<String> extractHouseLinks(Document doc) {
        List<String> links = new ArrayList<>();
        Elements elements = doc.select(".link-and-anchor.visuallyHidden"); // Ensure this selector is correct
        for (Element element : elements) {
            String href = element.attr("href");
            // Convert relative URL to absolute URL
            if (!href.startsWith("http")) {
                href = "https://www.redfin.com" + (href.startsWith("/") ? href : "/" + href);
            }
            System.out.println("Extracted link: " + href); // Debug output
            links.add(href);
        }
        return links;
    }


    // Fetch and parse a single house page
    public List<House> getHouseFromPage(String houseUrl) {
        try {
            Document houseDoc = Jsoup.connect(houseUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .referrer("http://www.google.com")
                    .timeout(12000) // Increase timeout to wait longer for a response
                    .followRedirects(true) // Ensure that any redirects are followed automatically
                    .get();
            List<House> houses = parseHouseDetails(houseDoc, houseUrl);
            System.out.println("Parsed " + houses.size() + " houses from URL: " + houseUrl); // Debug output
            return houses;
        } catch (IOException e) {
            System.out.println("Failed to fetch house details from " + houseUrl + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Parse house details from a document
    private List<House> parseHouseDetails(Document doc, String houseURL) {
        // System.out.println(doc.outerHtml());
        List<House> houses = new ArrayList<>();
        try {
            // house page: COST
            String cost = doc.select(".stat-block[data-rf-test-id='abp-price'] .statsValue").text().replace("$", "").replace(",", "");
            int cleaned_cost = cost.isEmpty() ? -1 : Integer.parseInt(cost);

            // house page: BEDS
            String s_beds = doc.select(".stat-block[data-rf-test-id='abp-beds'] .statsValue").text();
            int cleaned_beds = s_beds.isEmpty() ? -1 : metricToInteger(s_beds);

            // house page: BATHS
            String s_baths = doc.select(".stat-block[data-rf-test-id='abp-baths'] .statsValue").text();
            float cleaned_baths = s_baths.isEmpty() ? -1 : metricToFloat(s_baths);

            // house page: SQFT
            String sqft = doc.select(".stat-block[data-rf-test-id='abp-sqFt'] .statsValue").text().replace(",", "");
            int cleaned_sqft = sqft.isEmpty() ? -1 : metricToInteger(sqft);

            // house page: FULL ADDRESS
            String streetAddress = doc.select("div[data-rf-test-id='abp-streetLine']").text();
            String cityStateZip = doc.select("div[data-rf-test-id='abp-cityStateZip']").text();
            String fullAddress = streetAddress.isEmpty() ? "Unknown address" : streetAddress + ", " + cityStateZip;

            // String houseLink = doc.parent().select("a").attr("href");

            List<PriceData> priceHistory = new ArrayList<>();
            Elements priceHistoryElements = doc.select("div.price-col.number");
            Elements dateElements = doc.select("div.col-4 p:first-of-type");

//            for (int i = 0; i < priceHistoryElements.size(); i++) {
//                Element priceElement = priceHistoryElements.get(i);
//                String price = priceElement.text().replace("$", "").replace(",", "");
//
//                String date = (i < dateElements.size()) ? dateElements.get(i).text() : "Unknown date";
//
//                priceHistory.add(new PriceData(price, date));
//            }

            // house page: SCHOOL DISTRICT
            String schoolDistrict = doc.select("div.super-group-content div.amenity-group:has(div.title:contains(School Information)) ul li span.entryItemContent:contains(School District Name) span").text();
            String cleaned_school_district = "";

            // house page: YEAR BUILT
            int yearBuilt = metricToInteger(doc.select("div.table-row:has(span.table-label:contains(Year Built)) .table-value").text());
            int cleaned_year_built = 0;

            // house page: MARKET COMP SCORE
            String marketCompetitionString = doc.select("div#compete-score .scoreTM .score").text();
            int cleaned_market_comp = marketCompetitionString.isEmpty() ? -1 : metricToInteger(marketCompetitionString);

            // house page: WALK SCORE
            // System.out.println(doc.select("div.transport-icon-and-percentage.walkscore").outerHtml());
            String walkScoreString = doc.select("div.transport-icon-and-percentage.walkscore div[data-rf-test-name='ws-percentage'] span.value").text();
            int cleaned_walk_score = walkScoreString.isEmpty() ? -1 : metricToInteger(walkScoreString);

            // house page: TRANSIT SCORE
            String transitScoreString = doc.select("div.transport-icon-and-percentage.transitscore div[data-rf-test-name='ws-percentage'] span.value").text();
            int cleaned_transit_score = transitScoreString.isEmpty() ? -1 : metricToInteger(transitScoreString);

            // house page: BIKE SCORE
            String bikeScoreString = doc.select("div.transport-icon-and-percentage.bikescore div[data-rf-test-name='ws-percentage'] span.value").text();
            int cleaned_bike_score = bikeScoreString.isEmpty() ? -1 : metricToInteger(bikeScoreString);


            houses.add(new House(
                    cleaned_cost,               // int cost
                    cleaned_beds,               // int bed
                    cleaned_baths,              // float bath
                    cleaned_sqft,               // int sqft
                    fullAddress,                // String address
                    houseURL,                   // String URL
                    cleaned_school_district,    // String schoolDistrict
                    cleaned_year_built,         // int yearBuilt
                    cleaned_market_comp,        // int marketCompetition
                    cleaned_walk_score,         // int walkScore
                    cleaned_transit_score,      // int transitScore
                    cleaned_bike_score,         // int bikeScore
                    priceHistory                // List<PriceData> priceHistory
            ));
        } catch (Exception e) {
            System.out.println("Error parsing house details: " + e.getMessage());
        }
        return houses;
    }



    // Helper method to convert metric strings to integers
    public static int metricToInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;  // Return -1 if conversion fails
        }
    }

    // Helper method to convert metric strings to floats
    public static float metricToFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return -1.0f;  // Return -1.0 if conversion fails
        }
    }
}
