//not done, needs work

//this determines if two houses are similar enough to have an edge
public static boolean areSimilar(House house1, House house2) {
    // Define similarity thresholds for each attribute
    int priceThreshold = 50000;
    int bedroomsThreshold = 1;
    int bathroomsThreshold = 1;
    int squareFeetThreshold = 200;
    String locationThreshold = "Same";

    // Compare each attribute and return true if within thresholds, false otherwise
    if (Math.abs(house1.price - house2.price) <= priceThreshold &&
            Math.abs(house1.bedrooms - house2.bedrooms) <= bedroomsThreshold &&
            Math.abs(house1.bathrooms - house2.bathrooms) <= bathroomsThreshold &&
            Math.abs(house1.squareFeet - house2.squareFeet) <= squareFeetThreshold &&
            house1.location.equals(house2.location)) {
        return true;
    }
    return false;
}



//this will make the graph using the houses
//
public createPropertyGraph (arraylist<house>houselist) {
    //create adjacency list (dont forget) HERE
    for (int i = 0; i < houses.size(); i++) {
        for (int j = i + 1; j < houses.size(); j++) {
            House house1 = houses.get(i);
            House house2 = houses.get(j);
            if (House.areSimilar(house1, house2)) { //make sure to add edge to the adjaceny list
                System.out.println("Houses " + (i + 1) + " and " + (j + 1) + " should have an edge based on similarity.");

            }
        }
    }
    //return the adjaceny list (which is the graph)
}




public class GirvanNewmanAlgorithm {

    // Class to represent a graph node
    static class Node {
        int id;
        List<Integer> neighbors;

        public Node(int id) {
            this.id = id;
            this.neighbors = new ArrayList<>();
        }
    }

    // Function to perform depth-first search and calculate edge betweenness
    static double calculateEdgeBetweenness(List<Node> graph, int numNodes, int[][] adjacencyMatrix) {
        double[] edgeBetweenness = new double[numNodes];
        double maxBetweenness = Double.NEGATIVE_INFINITY;

        // Perform BFS for each node as source
        for (Node sourceNode : graph) {
            int source = sourceNode.id;
            Queue<Integer> queue = new LinkedList<>();
            boolean[] visited = new boolean[numNodes];
            Arrays.fill(visited, false);
            int[] distance = new int[numNodes];
            Arrays.fill(distance, -1);
            int[] parent = new int[numNodes];
            Arrays.fill(parent, -1);
            int[] numShortestPaths = new int[numNodes];
            Arrays.fill(numShortestPaths, 0);

            visited[source] = true;
            distance[source] = 0;
            numShortestPaths[source] = 1;
            queue.add(source);

            // BFS traversal
            while (!queue.isEmpty()) {
                int u = queue.poll();
                for (int v : graph.get(u).neighbors) {
                    if (!visited[v]) {
                        visited[v] = true;
                        distance[v] = distance[u] + 1;
                        queue.add(v);
                    }
                    if (distance[v] == distance[u] + 1) {
                        numShortestPaths[v] += numShortestPaths[u];
                        parent[v] = u;
                    }
                }
            }

            // Calculate edge betweenness
            double[] delta = new double[numNodes];
            Arrays.fill(delta, 0);
            while (!queue.isEmpty()) {
                int w = queue.poll();
                for (int v : graph.get(w).neighbors) {
                    if (distance[v] == distance[w] + 1) {
                        delta[v] += (numShortestPaths[v] / (double) numShortestPaths[w]) * (1 + delta[w]);
                    }
                }
                if (w != source) {
                    edgeBetweenness[parent[w]] += delta[w];
                    maxBetweenness = Math.max(maxBetweenness, edgeBetweenness[parent[w]]);
                }
            }
        }
        return maxBetweenness;
    }

    // Function to remove edge with highest betweenness
    static void removeEdgeWithHighestBetweenness(List<Node> graph, int[][] adjacencyMatrix) {
        double maxBetweenness = Double.NEGATIVE_INFINITY;
        int u = -1, v = -1;

        // Iterate over all edges to find the one with highest betweenness
        for (int i = 0; i < graph.size(); i++) {
            for (int neighbor : graph.get(i).neighbors) {
                if (adjacencyMatrix[i][neighbor] == 1) {
                    adjacencyMatrix[i][neighbor] = 0;
                    adjacencyMatrix[neighbor][i] = 0;
                    double betweenness = calculateEdgeBetweenness(graph, graph.size(), adjacencyMatrix);
                    if (betweenness > maxBetweenness) {
                        maxBetweenness = betweenness;
                        u = i;
                        v = neighbor;
                    }
                    adjacencyMatrix[i][neighbor] = 1;
                    adjacencyMatrix[neighbor][i] = 1;
                }
            }
        }

        // Remove edge with highest betweenness
        if (u != -1 && v != -1) {
            graph.get(u).neighbors.remove((Integer) v);
            graph.get(v).neighbors.remove((Integer) u);
        }
    }

    // Function to find communities using Girvan-Newman algorithm
    static List<List<Integer>> findCommunities(List<Node> graph) {
        List<List<Integer>> communities = new ArrayList<>();
        int numEdges = 0;

        // Initialize adjacency matrix
        int numNodes = graph.size();
        int[][] adjacencyMatrix = new int[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            for (int neighbor : graph.get(i).neighbors) {
                adjacencyMatrix[i][neighbor] = 1;
                adjacencyMatrix[neighbor][i] = 1;
                numEdges++;
            }
        }

        // Main loop of Girvan-Newman algorithm
        while (numEdges > 0) {
            double initialBetweenness = calculateEdgeBetweenness(graph, numNodes, adjacencyMatrix);
            removeEdgeWithHighestBetweenness(graph, adjacencyMatrix);
            double currentBetweenness = calculateEdgeBetweenness(graph, numNodes, adjacencyMatrix);
            if (currentBetweenness < initialBetweenness) {
                // Found a community, so add it to the list
                List<Integer> community = new ArrayList<>();
                boolean[] visited = new boolean[numNodes];
                for (int i = 0; i < numNodes; i++) {
                    if (!visited[i]) {
                        Queue<Integer> queue = new LinkedList<>();
                        queue.add(i);
                        while (!queue.isEmpty()) {
                            int node = queue.poll();
                            visited[node] = true;
                            community.add(node);
                            for (int neighbor : graph.get(node).neighbors) {
                                if (!visited[neighbor]) {
                                    queue.add(neighbor);
                                }
                            }
                        }
                        communities.add(community);
                    }
                }
            }
            numEdges--;
        }
        return communities;
    }


}

