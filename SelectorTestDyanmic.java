import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SelectorTest {

    private WebDriver driver;

    public SelectorTest() {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");

        // Initialize ChromeDriver
        driver = new ChromeDriver();
    }

    public void testSelector(String url, String selector) {
        // Navigate to the URL
        driver.get(url);

        // Find elements using the provided CSS selector
        WebElement element = driver.findElement(By.cssSelector(selector));

        // Print the text of the found element
        System.out.println("Found value: " + element.getText());
    }

    public static void main(String[] args) {
        // Replace this URL with the specific house page URL you want to test
        String url = "https://www.redfin.com/PA/Philadelphia/270-E-Bringhurst-St-19144/home/38320274";
        
        String selector = ".PropertyHistoryEventRow .col-4 p:first-of-type";

        SelectorTest tester = new SelectorTest();

        // Test the selector
        tester.testSelector(url, selector);
    }
}
