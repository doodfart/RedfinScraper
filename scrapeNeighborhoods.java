import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public LinkedList<String> scrapeNeighborhoods(String url) {
    LinkedList<String> descriptions = new LinkedList<>();
    try {
        Document doc = Jsoup.connect(url).get();
        for (Element paragraph : doc.select("p.description")) { // insert html of description'
            descriptions.add(paragraph.text());
        }
    } catch (IOException e) {
        System.out.println("Failed to fetch page: " + e.getMessage());
    }
    return descriptions;
}
