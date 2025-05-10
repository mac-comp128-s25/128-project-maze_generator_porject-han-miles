import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.macalester.graphics.CanvasWindow;
import java.awt.Color;
import edu.macalester.graphics.Line;

public class RecursiveBacktrackingAlgorithmGenerator {
    private final List<Node> nodes;
    private final List<Edge> edges;
    private final Random random;
    private final int size;
    private ArrayList<String> directions;

    private boolean[][] north;     // is there a wall to north of cell i, j
    private boolean[][] east;
    private boolean[][] south;
    private boolean[][] west;
    private CanvasWindow canvas;

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
        // nodes.get(random.nextInt(size));
        north = new boolean[size + 1][size + 1];
        south = new boolean[size + 1][size + 1];
        east = new boolean[size + 1][size + 1];
        west = new boolean[size + 1][size + 1];
        for (int x = 1; x < size; x++) {
            for (int y = 1; y < size; y++) {
                nodes.add(new Node(x, y));
                north[x][y] = true; // set up grid of boxes 
                south[x][y] = true;
                east[x][y] = true;
                west[x][y] = true;

            }
        }
        
    }

    public void generateMaze(Node nodeStart){
        Collections.shuffle(directions); // randomizes directions for each node
        System.out.println(directions);
        Node nodeChecking = null;
        for (int i = 0; i < directions.size(); i++){ // checks each direction for availablity
            if (directions.get(i) == "N"){
                if (insideBounds(nodeStart.x, nodeStart.y + 1)) { // if it is in bounds
                    nodeChecking = getNode(nodeStart.x, nodeStart.y + 1);
                    if (!nodeChecking.marked){ // if not allready marked
                        north[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        nodeStart.marked = true;
                        System.out.println("got here __North!__");
                        System.out.println(nodeChecking.x);
                        System.out.println(nodeChecking.y);
                        generateMaze(nodeChecking);
                    } 
                }
            }
            else if (directions.get(i) == "S"){
                if (insideBounds(nodeStart.x, nodeStart.y - 1)) {
                    nodeChecking = getNode(nodeStart.x, nodeStart.y - 1);
                    if (!nodeChecking.marked){
                        south[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        nodeStart.marked = true;
                        System.out.println("got here __South!__");
                        System.out.println(nodeChecking.x);
                        System.out.println(nodeChecking.y);
                        generateMaze(nodeChecking);
                    }
                }
            }
            else if (directions.get(i) == "E"){
                if (insideBounds(nodeStart.x + 1, nodeStart.y)) {
                    nodeChecking = getNode(nodeStart.x + 1, nodeStart.y);
                    if (!nodeChecking.marked){
                        east[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        nodeStart.marked = true;
                        System.out.println("got here __East!__");
                        System.out.println(nodeChecking.x);
                        System.out.println(nodeChecking.y);
                        generateMaze(nodeChecking);
                    }
                }
            }
            else if (directions.get(i) == "W"){
                if (insideBounds(nodeStart.x - 1, nodeStart.y)) {
                    nodeChecking = getNode(nodeStart.x - 1, nodeStart.y);
                    if (!nodeChecking.marked){
                        west[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        nodeStart.marked = true;
                        System.out.println("got here __West!__");
                        System.out.println(nodeChecking.x);
                        System.out.println(nodeChecking.y);
                        generateMaze(nodeChecking);
                    }
                }
            }
        }
    }

    public void drawMaze(CanvasWindow canvas, int nodeRadius){
        this.canvas = canvas;
        for (int x = 1; x <= size; x++) {
            for (int y = 0; y <= size; y++) {
                if (south[x][y]) {
                    Line l = new Line(scaleX(x), scaleY(y), scaleX(x + 1), scaleY(y));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    canvas.add(l);
                }
                if (north[x][y]) { 
                    Line l = new Line(scaleX(x), scaleY(y + 1), scaleX(x + 1), scaleY(y + 1));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    canvas.add(l);
                }
                if (west[x][y]) {
                    Line l = new Line(scaleX(x), scaleY(y), scaleX(x), scaleY(y + 1));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    canvas.add(l);
                }
                if (east[x][y]) {
                    Line l = new Line(scaleX(x + 1), scaleY(y), scaleX(x + 1), scaleY(y + 1));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    canvas.add(l);
                }
            }
        }
    }

    private boolean insideBounds(int x, int y){
        return (!(x < 1 || x > size + 1 || y < 0 || y > size + 1));
    }

    /**
     * method to get the nodes and edges of the maze
     * @return a list of nodes and edges
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * method to get a node at a specific position
     * @param x the x of the node
     * @param y the y of the node
     * @return the node at the specified position
     */
    public Node getNode(int x, int y){
        return nodes.get(x + y * size);
    }

    public Node getRandomNode(){
        return nodes.get(random.nextInt(size));
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
     * each node has a x and yumn position
     */
    public static class Node {
        int x, y;
        boolean marked;

        Node(int x, int y) {
            this.x = y;
            this.x = y;
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
    private double scaleX(double x) { return canvas.getWidth()  * (x) / (size+2); }
    private double scaleY(double y) { return canvas.getHeight() * ((size+2) - y) / (size+2); }
    
}