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

    private static class Node {
        int id;
        List<Node> neighbors;

        Node(int id) {
            this.id = id;
            this.neighbors = new ArrayList<>();
        }
    }

    private static class Edge {
        Node nodeA;
        Node nodeB;

        Edge(Node nodeA, Node nodeB) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }
    }
}
