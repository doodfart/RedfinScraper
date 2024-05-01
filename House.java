import java.util.List;

public class House {
    // attributes
    public int cost;
    public int bed;
    public float bath;
    public int sqft;
    public String address;
    public String houseURL;
    public String schoolDistrict;
    public String publicFacts;
    public int yearBuilt;
    public int marketCompetition;
    public int walkScore;
    public int transitScore;
    public int bikeScore;

    public List<PriceData> priceHistory;

    // constructor
    public House(int cost, int bed, float bath, int sqft, String address, String houseURL,
                 String schoolDistrict, int yearBuilt, int marketCompetition,
                 int walkScore, int transitScore, int bikeScore, List<PriceData> priceHistory) {
        this.cost = cost;
        this.bed = bed;
        this.bath = bath;
        this.sqft = sqft;
        this.address = address;
        this.houseURL = houseURL;
        this.schoolDistrict = schoolDistrict;
        this.yearBuilt = yearBuilt;
        this.marketCompetition = marketCompetition;
        this.walkScore = walkScore;
        this.transitScore = transitScore;
        this.bikeScore = bikeScore;
        this.priceHistory = priceHistory;
    }
}
