import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SimilarityCoef2 {

    // Node class for graph representation
    static class Node {
        int id;
        House house;
        List<Integer> neighbors;

        public Node(int id, House house) {
            this.id = id;
            this.house = house;
            this.neighbors = new ArrayList<>();
        }
    }

    // Method to determine if two houses should have an edge based on similarity
    static boolean areSimilar(House house1, House house2) {
        int priceThreshold = 50000;
        int bedroomsThreshold = 1;
        int bathroomsThreshold = 1;
        int squareFeetThreshold = 200;
        String locationThreshold = "Same County";

        return Math.abs(house1.cost - house2.cost) <= priceThreshold &&
                Math.abs(house1.bed - house2.bed) <= bedroomsThreshold &&
                Math.abs(house1.bath - house2.bath) <= bathroomsThreshold &&
                Math.abs(house1.sqft - house2.sqft) <= squareFeetThreshold &&
                house1.county.equals(house2.county);
    }

    // Method to create a property graph from a list of houses
    static List<Node> createPropertyGraph(ArrayList<House> houses) {
        List<Node> graph = new ArrayList<>();
        for (int i = 0; i < houses.size(); i++) {
            graph.add(new Node(i, houses.get(i)));
        }

        for (int i = 0; i < graph.size(); i++) {
            for (int j = i + 1; j < graph.size(); j++) {
                if (areSimilar(graph.get(i).house, graph.get(j).house)) {
                    graph.get(i).neighbors.add(j);
                    graph.get(j).neighbors.add(i);
                }
            }
        }
        return graph;
    }

    // Main method to execute the application logic
    public static void main(String[] args) {
        RealtorsParser parser = new RealtorsParser();  // Assuming RealtorsParser is implemented elsewhere
        LinkedList<House> houses = parser.collectAllHouses();
        List<Node> graph = createPropertyGraph(houses);

        GirvanNewmanAlgorithm gna = new GirvanNewmanAlgorithm();
        List<List<Integer>> communities = gna.findCommunities(graph);

        // Output communities
        for (List<Integer> community : communities) {
            System.out.println("Community:");
            for (Integer nodeId : community) {
                Node node = graph.get(nodeId);
                System.out.println(node.house.address + " - " + node.house.cost);
            }
        }
    }
}

// Assuming GirvanNewmanAlgorithm class is defined elsewhere with all necessary methods

