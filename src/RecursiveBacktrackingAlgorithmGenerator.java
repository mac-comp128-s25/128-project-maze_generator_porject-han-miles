import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.w3c.dom.Node;

public class RecursiveBacktrackingAlgorithmGenerator {
    private final List<Node> nodes;
    private final List<Edge> edges;
    private final Random random;
    private final int size;
    private ArrayList<String> directions;
    private Node nodeCurrent;

    public RecursiveBacktrackingAlgorithmGenerator(int size){
        this.size = size;

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        random = new Random();
        directions = new ArrayList<>();
        directions.add("N");
        directions.add("S");
        directions.add("E");
        directions.add("W");
        nodes.get(random.nextInt(size));

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                nodes.add(new Node(row, col));
            }
        }
        
    }

    private boolean isNeighbor(Node nodeA, Node nodeB) {
        int rowDiff = Math.abs(nodeA.row - nodeB.row);
        int colDiff = Math.abs(nodeA.col - nodeB.col);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }


    public void generateMaze(Node nodeStart){
        Collections.shuffle(directions); // randomizes directions for each node
        Node nodeChecking = null;
        for (int i = 0; i < directions.size(); i++){ // checks each direction for availablity
            if (directions.get(i) == "N"){
                nodeChecking = getNode(nodeStart.row, nodeStart.col - 1);
            }
            else if (directions.get(i) == "S"){
                nodeChecking = getNode(nodeStart.row, nodeStart.col + 1);
            }
            else if (directions.get(i) == "E"){
                nodeChecking = getNode(nodeStart.row + 1, nodeStart.col);
            }
            else if (directions.get(i) == "W"){
                nodeChecking = getNode(nodeStart.row - 1, nodeStart.col);
            }
            else {
                return;
            }
        }
        if (nodeChecking.marked = false){
            nodeChecking.marked = true;
            generateMaze(nodeChecking);
        }
    }

    /**
     * method to get the nodes and edges of the maze
     * @return a list of nodes and edges
     */
    public List<Node> getNodes() {
        return nodes;
    }

    public Node getNode(int row, int col){
        return nodes.get(row * size + col);
    }

    /**
     * method to get the edges of the maze
     * @return a list of edges
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * class representing a node in the maze
     * each node has a row and column position
     */
    public static class Node {
        int row, col;
        boolean marked;

        Node(int row, int col) {
            this.row = row;
            this.col = col;
            this.marked = false;
        }
    }

    /**
     * class representing a edge in the maze
     * each edge connects two nodes
     */
    public static class Edge {
        Node nodeA;
        Node nodeB;

        Edge(Node nodeA, Node nodeB) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }
    }
    
}