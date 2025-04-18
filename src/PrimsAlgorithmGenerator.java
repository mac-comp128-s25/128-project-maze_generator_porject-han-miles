import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PrimsAlgorithmGenerator {
    private List<Node> nodes;
    private List<Edge> edges;
    private Random random;
    public PrimsAlgorithmGenerator(int size){
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        random = new Random();
        for (int i =0; i<size; i++) {
            nodes.add(new Node(i));
        }
    }

    public void generateMaze() {
        List<Node> inside = new ArrayList<>();
        List<Node> outside = new ArrayList<>(nodes);

        Node startNode = outside.remove(random.nextInt(outside.size()));
        inside.add(startNode);

        while (!outside.isEmpty()) {
            Node insideNode = inside.get(random.nextInt(inside.size()));
            Node outsideNode = outside.remove(random.nextInt(outside.size()));
            edges.add(new Edge(insideNode, outsideNode));
            inside.add(outsideNode);
        }
    }

    public void addRandomEdges(double probability) {
        for (Edge edge : edges) {
            Node nodeA = edge.nodeA;
            Node nodeB = edge.nodeB;

            List<Edge> potentialEdges = findUnconnectedEdges(nodeA, nodeB);

            int edgesToAdd = random.nextInt(4); // 0 to 3
            for (int i = 0; i < edgesToAdd && i < potentialEdges.size(); i++) {
                if (random.nextDouble() < probability) {
                    edges.add(potentialEdges.get(i));
                }
            }
        }
    }

    private List<Edge> findUnconnectedEdges(Node nodeA, Node nodeB) {
        List<Edge> potentialEdges = new ArrayList<>();
        for (Node neighborA : nodeA.neighbors) {
            for (Node neighborB : nodeB.neighbors) {
                if (!areConnected(neighborA, neighborB)) {
                    potentialEdges.add(new Edge(neighborA, neighborB));
                }
            }
        }
        return potentialEdges;
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
        int id;
        List<Node> neighbors;

        Node(int id) {
            this.id = id;
            this.neighbors = new ArrayList<>();
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
