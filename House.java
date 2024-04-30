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
    public String style;
    public String county;
    public int lotSize;
    public int yearBuilt;
    public int marketCompetition;
    public int walkScore;
    public int transitScore;
    public int bikeScore;
    public float buyerAgentFee;

    // constructor
    public House(int cost, int bed, float bath, int sqft, String address, String houseURL,
                 String schoolDistrict, String publicFacts, int lotSize, String style,
                 String county, int yearBuilt, float buyerAgentFee, int marketCompetition,
                 int walkScore, int transitScore, int bikeScore) {
        this.cost = cost;
        this.bed = bed;
        this.bath = bath;
        this.sqft = sqft;
        this.address = address;
        this.houseURL = houseURL;
        this.schoolDistrict = schoolDistrict;
        this.publicFacts = publicFacts;
        this.lotSize = lotSize;
        this.style = style;
        this.county = county;
        this.yearBuilt = yearBuilt;
        this.buyerAgentFee = buyerAgentFee;
        this.marketCompetition = marketCompetition;
        this.walkScore = walkScore;
        this.transitScore = transitScore;
        this.bikeScore = bikeScore;
    }
}