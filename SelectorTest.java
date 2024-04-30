import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class SelectorTest {

    private Document document;

    public SelectorTest(String url) {
        connectToWebsite(url);
    }

    private void connectToWebsite(String url) {
        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .get();
            System.out.println("Connected successfully to " + url);
            // Uncomment to see the HTML
            // System.out.println(document.html());
        } catch (IOException e) {
            System.out.println("Could not connect to " + url + ": " + e.getMessage());
        }
    }

    public void testSelector(String selector) {
        if (document != null) {
            Elements elements = document.select(selector);
            System.out.println("Testing selector: " + selector);
            if (!elements.isEmpty()) {
                for (Element element : elements) {
                    System.out.println("Found value: " + element.text());
                }
            } else {
                System.out.println("No elements found with selector: " + selector);
            }
        } else {
            System.out.println("Document not loaded, cannot test selector.");
        }
    }

    public static void main(String[] args) {
        SelectorTest tester = new SelectorTest("https://www.redfin.com/city/15502/PA/Philadelphia");
        tester.testSelector("span:contains(School District Name:)"); // Example selector
    }
}
