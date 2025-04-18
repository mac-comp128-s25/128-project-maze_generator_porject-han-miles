import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PrimsAlgorithmGenerator {
    private List<Node> nodes;
    private List<Edge> edges;
    private Random random;
    private int gridSize;
    public PrimsAlgorithmGenerator(int gridSize){
        this.gridSize = gridSize;
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        random = new Random();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                nodes.add(new Node(row, col));
            }
        }
    }

    public void generateMaze() {
        List<Node> inside = new ArrayList<>();
        List<Node> outside = new ArrayList<>(nodes);

        Node startNode = outside.remove(random.nextInt(outside.size()));
        inside.add(startNode);

        while (!outside.isEmpty()) {
            Node insideNode = inside.get(random.nextInt(inside.size()));
            Node outsideNode = getRandomNeighbor(insideNode, outside);

            if (outsideNode != null) {
                edges.add(new Edge(insideNode, outsideNode));
                inside.add(outsideNode);
                outside.remove(outsideNode);
            }
        }
    }

    private Node getRandomNeighbor(Node node, List<Node> outside) {
        List<Node> neighbors = new ArrayList<>();
        for (Node potentialNeighbor : outside) {
            if (isNeighbor(node, potentialNeighbor)) {
                neighbors.add(potentialNeighbor);
            }
        }
        return neighbors.isEmpty() ? null : neighbors.get(random.nextInt(neighbors.size()));
    }

    private boolean isNeighbor(Node nodeA, Node nodeB) {
        int rowDiff = Math.abs(nodeA.row - nodeB.row);
        int colDiff = Math.abs(nodeA.col - nodeB.col);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    public void addRandomEdges(double probability) {
        for (Node node : nodes) {
            for (Node neighbor : getPotentialNeighbors(node)) {
                if (!areConnected(node, neighbor) && random.nextDouble() < probability) {
                    edges.add(new Edge(node, neighbor));
                }
            }
        }
    }
    
    private List<Node> getPotentialNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int row = node.row;
        int col = node.col;
    
        if (row > 0) neighbors.add(getNodeAt(row - 1, col)); // Up
        if (row < gridSize - 1) neighbors.add(getNodeAt(row + 1, col)); // Down
        if (col > 0) neighbors.add(getNodeAt(row, col - 1)); // Left
        if (col < gridSize - 1) neighbors.add(getNodeAt(row, col + 1)); // Right
    
        return neighbors;
    }
    
    private Node getNodeAt(int row, int col) {
        return nodes.get(row * gridSize + col);
    }

    private boolean areConnected(Node nodeA, Node nodeB) {
        for (Edge edge : edges) {
            if ((edge.nodeA == nodeA && edge.nodeB == nodeB) ||
                (edge.nodeA == nodeB && edge.nodeB == nodeA)) {
                return true;
            }
        }
        return false;
    }
    
    public List<Node> getNodes() {
        return nodes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public static class Node {
        int row, col;

        Node(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public static class Edge {
        Node nodeA;
        Node nodeB;

        Edge(Node nodeA, Node nodeB) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }
    }
}
