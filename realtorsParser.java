import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class realtorsParser {
    private Document currentDoc;
    private String coreURL;
    private String baseUrl;

    private LinkedList<house> redfinHouses = new LinkedList<house>();

    public realtorsParser(String base) {
        coreURL = base;
        baseUrl = "https://www.redfin.com";
        try {
            currentDoc = Jsoup.connect(coreURL).get();
        } catch (IOException e) {
            System.out.println("Could not access link: " + e.getMessage());
        }
    }


    public LinkedList<house> collectAllHouses() {
        String curURL = this.coreURL + "page-";

        for (int i = 1; i < 10; i++) {
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

            Element page_sqft = h.select("span.bp-Homecard__LockedStat--value").first();
            String sqft;
            if (page_sqft == null) {
                sqft = "";
            } else {
                sqft = page_sqft.text();
            }
            int cleaned_sqft = metricToInteger(sqft.replace(",", ""));

            String addr = h.select("div.bp-Homecard__Address.flex.align-center.color-text-primary.font-body-xsmall-compact").first().text();
            String houseLink = h.parent().select("a").attr("href");
            String marketing = pullHouseMarketing(this.baseUrl + houseLink);
            redfinHouses.add(new house(cleaned_cost, cleaned_beds, cleaned_baths, cleaned_sqft, addr, this.baseUrl + houseLink, marketing));
        }

    }

    private static int metricToInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static float metricToFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String pullHouseMarketing(String url) {
        try {

            Document currentDoc = Jsoup.connect(url).get();
            Element div = currentDoc.select("div.remarks").select("span").first();
            if (div == null) {
                return "";
            } else {
                return div.text();
            }

        } catch (IOException e) {
            System.out.println("Could not access link: " + e.getMessage());
        }
        return "";
    }

    public static void calculateStatistics(LinkedList<house> allHouses) {
        if (allHouses == null || allHouses.isEmpty()) {
            System.out.println("No houses available.");
            return;
        }

        int totalCost = 0, totalBedrooms = 0, totalSqFt = 0;
        float totalBathrooms = 0.0f;
        house mostExpensive = allHouses.getFirst();
        house leastExpensive = allHouses.getFirst();

        for (house h : allHouses) {
            totalCost += h.cost;
            totalBedrooms += h.bed;
            totalBathrooms += h.bath;
            totalSqFt += h.sqft;

            if (h.cost > mostExpensive.cost) {
                mostExpensive = h;
            }

            if (h.cost < leastExpensive.cost) {
                leastExpensive = h;
            }
        }

        int numberOfHouses = allHouses.size();
        double avgCost = (double) totalCost / numberOfHouses;
        double avgBedrooms = (double) totalBedrooms / numberOfHouses;
        double avgBathrooms = totalBathrooms / numberOfHouses;
        double avgSqFt = (double) totalSqFt / numberOfHouses;

        System.out.println("Average Cost: $" + avgCost);
        System.out.println("Average Number of Bedrooms: " + avgBedrooms);
        System.out.println("Average Number of Bathrooms: " + avgBathrooms);
        System.out.println("Average Square Footage: " + avgSqFt + " sqft");
        System.out.println("Most Expensive House: " + mostExpensive.address + " Cost: $" + mostExpensive.cost);
        System.out.println("Least Expensive House: " + leastExpensive.address + " Cost: $" + leastExpensive.cost);
    }

    public void closestHouseMatch() {
        Scanner scanner = new Scanner(System.in);


        System.out.println("----------------------------------");
        System.out.println("In order to help you identify the best home for your needs " +
                "we're going to ask a series of questions that are core features of a home");

        System.out.println("Enter your budget: ");
        int budget = scanner.nextInt();

        System.out.println("Min bedrooms: ");
        int minBeds = scanner.nextInt();

        System.out.println("Min bathrooms: ");
        float minBaths = scanner.nextFloat();

        System.out.println("Min square feet: ");
        int minSqft = scanner.nextInt();

        double costWeight = 0.4;
        double bedWeight = 0.2;
        double bathWeight = 0.2;
        double sqftWeight = 0.2;

        house closestHouse = null;
        double minDistance = Double.MAX_VALUE;

        for (house h : redfinHouses) {
            // Calculate normalized differences
            double costDiff = Math.abs(h.cost - budget) / (double) budget;
            double bedDiff = Math.abs(h.bed - minBeds) / (double) minBeds;
            double bathDiff = Math.abs(h.bath - minBaths) / minBaths;
            double sqftDiff = Math.abs(h.sqft - minSqft) / (double) minSqft;

            double distance = costWeight * costDiff +
                    bedWeight * bedDiff +
                    bathWeight * bathDiff +
                    sqftWeight * sqftDiff;

            if (distance < minDistance) {
                minDistance = distance;
                closestHouse = h;
            }
        }

        if (closestHouse != null) {
            System.out.println("The house closest to your preferences is: ");
            System.out.println(closestHouse);
            System.out.println("-------------------------------");
        } else {
            System.out.println("No houses match your preferences closely.");
        }
    }


    public void printOutAll() {
        for (house h : redfinHouses) {
            System.out.println("Cost: " + h.cost + ", Addr: " + h.address + " Bed " + h.bed);
        }
    }
}
