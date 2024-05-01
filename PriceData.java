
public class PriceData {
    private String price;
    private String date; // Optionally, consider storing the date if historical data includes this

    public PriceData(String price, String date) {
        this.price = price;
        this.date = date;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "PriceData{" +
                "price='" + price + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
