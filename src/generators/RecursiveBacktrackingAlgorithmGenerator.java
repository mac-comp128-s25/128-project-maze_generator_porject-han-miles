package generators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Ellipse;

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
    private ArrayList <Line> lines;
    // int repeated = 0;
    Node startNode;

    public RecursiveBacktrackingAlgorithmGenerator(int size){
        this.size = size;

        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        random = new Random();
        directions = new ArrayList<>();
        lines = new ArrayList<>();
        directions.add("N");
        directions.add("S");
        directions.add("E");
        directions.add("W");
        // nodes.get(random.nextInt(size));
        north = new boolean[size + 1][size + 1];
        south = new boolean[size + 1][size + 1];
        east = new boolean[size + 1][size + 1];
        west = new boolean[size + 1][size + 1];
        for (int yPos = 0; yPos < size; yPos++) {
            for (int xPos = 0; xPos < size; xPos++) {
                nodes.add(new Node(xPos, yPos));
                north[xPos][yPos] = true; // set up grid of boxes 
                south[xPos][yPos] = true;
                east[xPos][yPos] = true;
                west[xPos][yPos] = true;

            }
        }
        
    }

    public void generateMaze(Node nodeStart){
        if (nodeStart == null){
            nodeStart = nodes.get(0);
        }
        Collections.shuffle(directions); // randomizes directions for each node
        System.out.println(directions);
        Node nodeChecking = null;
        nodeStart.marked = true;

        for (int i = 0; i < directions.size(); i++){ // checks each direction for availablity
            if (directions.get(i) == "N"){
                if (insideBounds(nodeStart.x, nodeStart.y + 1)) { // if it is in bounds
                    nodeChecking = getNode(nodeStart.x, nodeStart.y + 1);
                    if (!nodeChecking.marked){ // if not allready marked
                        north[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        south[nodeStart.x][nodeStart.y + 1] = false; // remove for ajacent node to prevent gap being drawn over
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        System.out.println("got here __North!__");
                        System.out.println("X:");
                        System.out.println(nodeStart.x);
                        System.out.println("Y:");
                        System.out.println(nodeStart.y);
                        nodeChecking.marked = true;
                        generateMaze(nodeChecking);
                    } 
                }
            }
            else if (directions.get(i) == "S"){
                if (insideBounds(nodeStart.x, nodeStart.y - 1)) {
                    nodeChecking = getNode(nodeStart.x, nodeStart.y - 1);
                    if (!nodeChecking.marked){
                        south[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        north[nodeStart.x][nodeStart.y - 1] = false;
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        System.out.println("got here __South!__");
                        System.out.println("X:");
                        System.out.println(nodeStart.x);
                        System.out.println("Y:");
                        System.out.println(nodeStart.y);
                        nodeChecking.marked = true;
                        generateMaze(nodeChecking);
                    }
                }
            }
            else if (directions.get(i) == "E"){
                if (insideBounds(nodeStart.x + 1, nodeStart.y)) {
                    nodeChecking = getNode(nodeStart.x + 1, nodeStart.y);
                    if (!nodeChecking.marked){
                        east[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        west[nodeStart.x + 1][nodeStart.y] = false;
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        System.out.println("got here __East!__");
                        System.out.println("X:");
                        System.out.println(nodeStart.x);
                        System.out.println("Y:");
                        System.out.println(nodeStart.y);
                        nodeChecking.marked = true;
                        generateMaze(nodeChecking);
                    }
                }
            }
            else if (directions.get(i) == "W"){
                if (insideBounds(nodeStart.x - 1, nodeStart.y)) {
                    nodeChecking = getNode(nodeStart.x - 1, nodeStart.y);
                    if (!nodeChecking.marked){
                        west[nodeStart.x][nodeStart.y] = false; // remove the wall in the direction moved
                        east[nodeStart.x - 1][nodeStart.y] = false;
                        edges.add(new Edge(nodeStart,nodeChecking)); // create edge between nodes
                        System.out.println("got here __West!__");
                        System.out.println("X:");
                        System.out.println(nodeStart.x);
                        System.out.println("Y:");
                        System.out.println(nodeStart.y);
                        nodeChecking.marked = true;
                        generateMaze(nodeChecking);
                    }
                }
            }
        }
    }

    public ArrayList<Line> drawMaze(CanvasWindow canvas){
        this.canvas = canvas;

        // Ellipse n = new Ellipse(scaleX(startNode.x - 0.55), scaleY(startNode.y - 0.45), 20, 20); // nodes
        // n.setFilled(true);
        // canvas.add(n);
        checkForBoxes(); // makes sure rare instances of closed off boxes don't happen

        north[size-1][size-1] = false;
        south[1][1] = false;
        

        for (int x = 0; x <= size; x++) {
            for (int y = 0; y <= size; y++) {
                /* draw nodes */
                // Ellipse s = new Ellipse(scaleX(x - 0.55), scaleY(y - 0.45), 20, 20);
                //     s.setFillColor(Color.RED);
                //     if (insideBounds(x, y)){
                //         if (getNode(x, y).marked){ s.setFillColor(Color.green); }
                //     }
                //     s.setFilled(true);
                //     canvas.add(s);

                if (south[x][y]) {
                    Line l = new Line(scaleX(x), scaleY(y), scaleX(x + 1), scaleY(y));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    l.setStrokeWidth(30);
                    canvas.add(l);
                    lines.add(l);
                }
                if (north[x][y]) { 
                    Line l = new Line(scaleX(x), scaleY(y + 1), scaleX(x + 1), scaleY(y + 1));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    l.setStrokeWidth(30);
                    canvas.add(l);
                    lines.add(l);
                }
                if (west[x][y]) {
                    Line l = new Line(scaleX(x), scaleY(y), scaleX(x), scaleY(y + 1));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    l.setStrokeWidth(30);
                    canvas.add(l);
                    lines.add(l);
                }
                if (east[x][y]) {
                    Line l = new Line(scaleX(x + 1), scaleY(y), scaleX(x + 1), scaleY(y + 1));
                    l.setStrokeColor(Color.BLACK);
                    l.setStroked(true);
                    l.setStrokeWidth(30);
                    canvas.add(l);
                    lines.add(l);
                }
            }
        }
        
        
        /* show path of nodes */

        // for (Edge edge : edges){
        //     double sX = scaleX(edge.nodeA.x + .5);
        // double sY = scaleY(edge.nodeA.y + .5);
        // double fX = scaleX(edge.nodeB.x + .5);
        // double fY = scaleY(edge.nodeB.y + .5);
        // Line l = new Line(sX,sY,fX,fY);
        // l.setStrokeColor(Color.RED);
        // l.setStroked(true);
        // l.setStrokeWidth(5);
        // canvas.add(l);
        // }

        north[size][size-1] = false;

        return lines;
    }
    /*
     * handles very rare cases where  nodes will get marked while still being boxed off, making them not accesable
     * cuts a random hole in the box
     */
    private void checkForBoxes(){
        
        for (int n = 0; n < size*size; n++){
            Node cn = nodes.get(n);
            if ((north[cn.x][cn.y]) && (south[cn.x][cn.y]) && (east[cn.x][cn.y]) && (west[cn.x][cn.y])){
                System.out.println("box found!!");
                System.out.println(cn.x);
                System.out.println(cn.y);
                Collections.shuffle(directions);
                cn.marked = false;
                System.out.println(directions);
                for (int i = 0; i < directions.size(); i++){
                    if (!cn.marked){
                    if (directions.get(i) == "N"){
                        if (insideBounds(cn.x, cn.y + 1)) {
                            north[cn.x][cn.y] = false; 
                            south[cn.x][cn.y + 1] = false;
                            edges.add(new Edge(cn, getNode(cn.x, cn.y + 1)));
                        }
                    }
                    else if (directions.get(i) == "S"){
                        if (insideBounds(cn.x, cn.y - 1)) {
                            north[cn.x][cn.y] = false; 
                            south[cn.x][cn.y - 1] = false;
                            edges.add(new Edge(cn, getNode(cn.x, cn.y - 1)));

                        }
                    }
                    else if (directions.get(i) == "E"){
                        if (insideBounds(cn.x + 1, cn.y)) {
                            north[cn.x][cn.y] = false; 
                            south[cn.x + 1][cn.y] = false;
                            edges.add(new Edge(cn, getNode(cn.x + 1, cn.y)));

                        }
                    }
                    else if (directions.get(i) == "W"){
                        if (insideBounds(cn.x - 1, cn.y)) {
                            north[cn.x][cn.y] = false; 
                            south[cn.x - 1][cn.y] = false;
                            edges.add(new Edge(cn, getNode(cn.x - 1, cn.y)));

                        }
                    }
                    }
                }
            }
        }
    }
        

    private boolean insideBounds(int x, int y){
        return (!(x < 0 || x >= size || y < 0 || y >= size));
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
        return nodes.get((x) + ((size) * (y)));
    }

    public Node getRandomNode(){
        Node randNode = nodes.get(random.nextInt((size) * (size)));
        if (randNode.x == 0){
            randNode = getNode(randNode.x+1, randNode.y);
        }
        if (randNode.y == 0){
            randNode = getNode(randNode.x, randNode.y+1);
        }
        startNode = randNode;
        return randNode;
        
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
            this.x = x;
            this.y = y;
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
    private double scaleX(double x) { return canvas.getWidth()  * (x) / (size); }
    private double scaleY(double y) { return canvas.getHeight() * ((size) - (y)) / (size) ; }
    // massive check for boxes, goes out of bonds
    // ((north[cn.x][cn.y] || south[cn.x][cn.y+1]) && (south[cn.x][cn.y] || north[cn.x][cn.y-1]) && (east[cn.x][cn.y] || west[cn.x + 1][cn.y]) && (west[cn.x][cn.y] || east[cn.x - 1][cn.y]))
    
}