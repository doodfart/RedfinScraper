import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RealtorsParser {
    private String coreURL = "https://www.redfin.com/city/15502/PA/Philadelphia";
    private LinkedList<House> redfinHouses = new LinkedList<House>();  // List to store the collected house data

    public RealtorsParser() {
    }

    // Main method to collect all houses
    public LinkedList<House> collectAllHouses() {
        LinkedList<House> redfinHouses = new LinkedList<>();
        String baseURL = "https://www.redfin.com/city/15502/PA/Philadelphia";
        for (int i = 1; i <= 10; i++) { // Navigating through the first 10 pages
            String pageUrl = baseURL + "/page-" + i;
            System.out.println("Fetching URL: " + pageUrl); // Print the URL being accessed
            try {
                Document pageDoc = Jsoup.connect(pageUrl).get();
                List<String> houseLinks = extractHouseLinks(pageDoc);
                System.out.println("Found " + houseLinks.size() + " house links on page " + i); // Debug the number of links found
                for (String link : houseLinks) {
                    System.out.println("Processing link: " + link); // Debug output
                    List<House> houses = getHouseFromPage(link);
                    redfinHouses.addAll(houses);
                }
            } catch (IOException e) {
                System.out.println("Failed to fetch or parse page " + i + ": " + e.getMessage());
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
            Document houseDoc = Jsoup.connect(houseUrl).get();
            List<House> houses = parseHouseDetails(houseDoc);
            System.out.println("Parsed " + houses.size() + " houses from URL: " + houseUrl); // Debug output
            return houses;
        } catch (IOException e) {
            System.out.println("Failed to fetch house details from " + houseUrl + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Parse house details from a document
    private List<House> parseHouseDetails(Document doc) {
        List<House> houses = new ArrayList<>();
        Elements elements = doc.select(".bp-Homecard__Content");

        for (Element h : elements) {
            String cost = h.select(".bp-Homecard__Price--value").text();
            int cleaned_cost = Integer.parseInt(cost.replace("$", "").replace(",", ""));

            String s_beds = h.select("span:contains(bed)").first().text().split(" ")[0];
            int cleaned_beds = metricToInteger(s_beds);

            String s_baths = h.select("span:contains(bath)").first().text().split(" ")[0];
            float cleaned_baths = metricToFloat(s_baths);

            String sqft = h.select("span.bp-Homecard__LockedStat--value").first().text();
            int cleaned_sqft = metricToInteger(sqft.replace(",", ""));

            String address = h.select("div.bp-Homecard__Address.flex.align-center.color-text-primary.font-body-xsmall-compact").first().text();
            String houseLink = h.parent().select("a").attr("href");

            List<PriceData> priceHistory = new ArrayList<>();
            Elements priceHistoryElements = h.select("div.price-col.number");
            Elements dateElements = h.select("div.col-4 p:first-of-type");

            for (int i = 0; i < priceHistoryElements.size(); i++) { //needs revision (important)
                Element priceElement = priceHistoryElements.get(i);
                String price = priceElement.text().replace("$", "").replace(",", "");

                String date = (i < dateElements.size()) ? dateElements.get(i).text() : "Unknown date";

                priceHistory.add(new PriceData(price, date));
            }

            // house page: SCHOOL DISTRICT
            String schoolDistrict = h.select("div.super-group-content div.amenity-group:has(div.title:contains(School Information)) ul li span.entryItemContent:contains(School District Name) span").text();
            String cleaned_school_district = "";

            String publicFacts = h.select("div.sectionContainer[data-rf-test-id=\"publicRecords\"] .PublicRecordsBasicInfo").text();
            String cleaned_public_facts = "";

            int lotSize = metricToInteger(h.select(".lot-size").text().replace(",", "")); //this may be alr found under public facts remove(?)
            int cleaned_lot_size = 0;

            String style = h.select(".style").text(); //this may be alr found under public facts remove(?)
            String cleaned_style = "";

            String county = h.select(".county").text(); //this may be alr found under public facts remove(?)
            String cleaned_county = "";

            // house page: YEAR BUILT
            int yearBuilt = metricToInteger(h.select("div.table-row:has(span.table-label:contains(Year Built)) .table-value").text());
            int cleaned_year_built = 0;

            // REMOVE THIS
            float buyerAgentFee = metricToFloat(h.select(".buyer-agent-fee").text().replace("%", ""));
            float cleaned_buyer_agent = 0;

            // house page: MARKET COMP SCORE
            int marketCompetition = metricToInteger(h.select("div#compete-score .scoreTM .score").text());
            int cleaned_market_comp = 0;

            // house page: WALK SCORE
            int walkScore = metricToInteger(h.select("div.transport-icon-and-percentage.walkscore div[data-rf-test-name='ws-percentage'] span.value").text());
            int cleaned_walk_score = 0;

            // house page: TRANSIT SCORE
            int transitScore = metricToInteger(h.select("div.transport-icon-and-percentage.transitscore div[data-rf-test-name='ws-percentage'] span.value").text());
            int cleaned_transit_score = 0;

            // house page: BIKE SCORE
            int bikeScore = metricToInteger(h.select("div.transport-icon-and-percentage.bikescore div[data-rf-test-name='ws-percentage'] span.value").text());
            int cleaned_bike_score = 0;

            houses.add(new House(cleaned_cost, cleaned_beds, cleaned_baths, cleaned_sqft, address, houseLink, cleaned_school_district,
                    cleaned_public_facts, cleaned_lot_size, cleaned_style, cleaned_county, cleaned_year_built, cleaned_buyer_agent,
                    cleaned_market_comp, cleaned_walk_score, cleaned_transit_score, cleaned_bike_score, priceHistory));
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
