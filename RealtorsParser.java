import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;

public class RealtorsParser {
    private Document currentDoc;  // Document object to hold the current page being processed
    private String coreURL;       // Base URL for the site (can be extended for other cities or pages)

    private LinkedList<House> redfinHouses = new LinkedList<House>();  // List to store the collected house data

    // Constructor initializes the parser with a specific URL and attempts to fetch the initial page
    public RealtorsParser() {
        coreURL = "https://www.redfin.com/";
        try {
            currentDoc = Jsoup.connect("https://www.redfin.com/city/15502/PA/Philadelphia").get();
        } catch (IOException e) {
            System.out.println("Could not access link: " + e.getMessage());
        }
    }

    // Collects all houses from multiple pages
    public LinkedList<House> collectAllHouses() {
        String curURL = "https://www.redfin.com/city/15502/PA/Philadelphia/page-";

        for (int i = 1; i < 10; i++) {  // Example: navigating through the first 10 pages
            try {
                Document currentDoc = Jsoup.connect(curURL + i).get();
                getHousesfromPage(currentDoc);
            } catch (IOException e) {
                System.out.println("Could not access link: " + e.getMessage());
            }
        }
        return redfinHouses;
    }

    private void getHousesfromPage(Document curPage) {
        Elements elements = curPage.select(".bp-Homecard__Content");

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

            String schoolDistrict = h.select(".school-district").text();
            String cleaned_school_district = "";

            String publicFacts = h.select(".public-facts").text();
            String cleaned_public_facts = "";

            int lotSize = metricToInteger(h.select(".lot-size").text().replace(",", ""));
            int cleaned_lot_size = 0;

            String style = h.select(".style").text();
            String cleaned_style = "";

            String county = h.select(".county").text();
            String cleaned_county = "";

            int yearBuilt = metricToInteger(h.select(".year-built").text());
            int cleaned_year_built = 0;

            float buyerAgentFee = metricToFloat(h.select(".buyer-agent-fee").text().replace("%", ""));
            float cleaned_buyer_agent = 0;

            int marketCompetition = metricToInteger(h.select(".market-competition").text());
            int cleaned_market_comp = 0;

            int walkScore = metricToInteger(h.select(".walk-score").text());
            int cleaned_walk_score = 0;

            int transitScore = metricToInteger(h.select(".transit-score").text());
            int cleaned_transit_score = 0;

            int bikeScore = metricToInteger(h.select(".bike-score").text());
            int cleaned_bike_score = 0;

            redfinHouses.add(new House(cleaned_cost, cleaned_beds, cleaned_baths, cleaned_sqft, address, houseLink, cleaned_school_district,
                    cleaned_public_facts, cleaned_lot_size, cleaned_style, cleaned_county, cleaned_year_built, cleaned_buyer_agent,
                    cleaned_market_comp, cleaned_walk_score, cleaned_transit_score, cleaned_bike_score));
        }
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