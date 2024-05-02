public class house {

    public int cost;
    public int bed;
    public float bath;
    public int sqft;
    public String address;
    public String houseURL;

    public String marketingText;
    public house(int cost, int bed, float bath, int sqft,
                 String address, String houseURL, String marketingText) {
        this.cost = cost;
        this.bed = bed;
        this.bath = bath;
        this.sqft = sqft;
        this.address = address;
        this.houseURL = houseURL;
        this.marketingText = marketingText;
    }
    @Override
    public String toString() {
        return "The address of this home is: " + address + ". With a cost of " + cost + " and " +
                "square footage of " + sqft + ". It has " + bed + " beds and " + bath + " baths!";
    }
}
