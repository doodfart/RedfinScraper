import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import textanalysis.Corpus;
import textanalysis.Document;
import textanalysis.VectorSpaceModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class scrapeNeighborhoods {
    private String coreURL;
    private List<String> descriptions = new ArrayList<>();

    public scrapeNeighborhoods(String url) {
        this.coreURL = url;
    }

    public Document connectWithJsoup(String url) throws IOException, InterruptedException {
        // connection logic
        return Jsoup.connect(url).execute().parse();
    }

    public List<String> scrapeDescriptions() {
        // scraping logic
        return descriptions;
    }

    public static void main(String[] args) {
        scrapeNeighborhoods scraper = new scrapeNeighborhoods("https://www.homes.com/neighborhood-search/boston-ma/");
        List<String> descriptions = scraper.scrapeDescriptions();
        Corpus corpus = new Corpus();
        for (String description : descriptions) {
            corpus.addDocument(new Document(description));
        }
        VectorSpaceModel vsm = new VectorSpaceModel(corpus);
        // further logic
    }
}
