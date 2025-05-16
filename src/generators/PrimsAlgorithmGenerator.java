package generators;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Line;


public class PrimsAlgorithmGenerator {
    private final List<Node> nodes;
    private final List<Edge> edges;
    private final Random random;
    private final int gridSize;
    private Set<String> connections;
    /**
    * constractor
    * @param gridSize the size of the grid
    */
    public PrimsAlgorithmGenerator(int gridSize){
        this.gridSize = gridSize;
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        random = new Random();
        connections = new HashSet<>();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                nodes.add(new Node(row, col));
            }
        }
    }

    /**
     * Helper method to create a unique key for a connection between two nodes
     * @param nodeA the first node
     * @param nodeB the second node
     * @return a unique key for the connection
     */
    private String getConnectionKey(Node nodeA, Node nodeB) {
        int indexA = nodeA.row * gridSize + nodeA.col;
        int indexB = nodeB.row * gridSize + nodeB.col;
        if (indexA < indexB) {
            return indexA + "_" + indexB;
        } else {
            return indexB + "_" + indexA;
        }
    }

    /**
     * Helper method to build the connections set after edges are generated
     */
    private void buildConnectionsSet() {
        connections.clear(); 
        for (Edge edge : edges) {
            connections.add(getConnectionKey(edge.nodeA, edge.nodeB));
        }
    }

    /**
    * method to generate a maze using Prim's algorithm with a random starting point
    */
    public void generateMaze() {
        edges.clear(); 
        List<Node> inside = new ArrayList<>();
        List<Node> outside = new ArrayList<>(nodes);

        if (outside.isEmpty()) return; 

        Node startNode = outside.remove(random.nextInt(outside.size()));
        inside.add(startNode);

        List<Edge> potentialEdges = new ArrayList<>();
        findPotentialEdges(startNode, outside, potentialEdges);

        while (!outside.isEmpty() && !potentialEdges.isEmpty()) {
            Edge edge = potentialEdges.remove(random.nextInt(potentialEdges.size()));
            Node nodeA = edge.nodeA;
            Node nodeB = edge.nodeB;

            Node outsideNode = null;
            if (inside.contains(nodeA) && !inside.contains(nodeB)) {
                outsideNode = nodeB;
            } else if (inside.contains(nodeB) && !inside.contains(nodeA)) {
                outsideNode = nodeA;
            }

            if (outsideNode != null) {
                edges.add(edge);
                inside.add(outsideNode);
                outside.remove(outsideNode);
                potentialEdges.removeIf(e -> inside.contains(e.nodeA) && inside.contains(e.nodeB));
                findPotentialEdges(outsideNode, outside, potentialEdges);
            }
        }
         buildConnectionsSet(); 
    }


    /**
     * Finds potential edges connecting the given node to nodes in the outside list
     * and adds them to the potentialEdges list.
     * @param node the node to check for potential edges
     * @param outside the list of nodes outside the current maze
     * @param potentialEdges the list to add potential edges to
     */
    private void findPotentialEdges(Node node, List<Node> outside, List<Edge> potentialEdges) {
        for (Node potentialNeighbor : outside) {
            if (isNeighbor(node, potentialNeighbor)) {
                boolean alreadyExists = false;
                for(Edge existingEdge : potentialEdges) {
                    if ((existingEdge.nodeA == node && existingEdge.nodeB == potentialNeighbor) ||
                        (existingEdge.nodeA == potentialNeighbor && existingEdge.nodeB == node)) {
                        alreadyExists = true;
                        break;
                    }
                }
                if (!alreadyExists) {
                    potentialEdges.add(new Edge(node, potentialNeighbor));
                }
            }
        }
    }


    /**
     * method to check if two nodes are neighbors
     * @param nodeA the first node
     * @param nodeB the second node
     * @return true if they are neighbors, false otherwise
     */
    private boolean isNeighbor(Node nodeA, Node nodeB) {
        int rowDiff = Math.abs(nodeA.row - nodeB.row);
        int colDiff = Math.abs(nodeA.col - nodeB.col);
        return (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
    }

    /**
     * method to add random edges to the maze
     * @param probability the probability of adding an edge
     */
    public void addRandomEdges(double probability) {
        int addedCount = 0;
        for (Node node : nodes) {
            for (Node neighbor : getPotentialNeighbors(node)) {
                if (!connections.contains(getConnectionKey(node, neighbor)) && random.nextDouble() < probability) {
                    edges.add(new Edge(node, neighbor));
                    connections.add(getConnectionKey(node, neighbor));
                    addedCount++;
                }
            }
        }
    }
    
    /**
     * method to get potential neighbors of a node
     * @param node the node to get neighbors for
     * @return a list of potential neighbors
     */
    public List<Node> getPotentialNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int row = node.row;
        int col = node.col;
    
        if (row > 0) neighbors.add(getNodeAt(row - 1, col)); // Up
        if (row < gridSize - 1) neighbors.add(getNodeAt(row + 1, col)); // Down
        if (col > 0) neighbors.add(getNodeAt(row, col - 1)); // Left
        if (col < gridSize - 1) neighbors.add(getNodeAt(row, col + 1)); // Right
    
        return neighbors;
    }
    

    /**
     * method to get a node at a specific position
     * @param row the row of the node
     * @param col the column of the node
     * @return the node at the specified position
     */
    private Node getNodeAt(int row, int col) {
        if (row >= 0 && row < gridSize && col >= 0 && col < gridSize) {
            return nodes.get(row * gridSize + col);
        }
        return null;
    }



    /**
     * method to generate the lines of maze
     * @param canvasWidth the width of canvas to draw on
     * @param canvasHeight the height of canvas to draw on
     * @param wallThickness The desired stroke width for the wall lines.
     * @return A List of Line objects representing the maze walls.
     */
    public List<Line> generateMazeLines(int canvasWidth, int canvasHeight, double wallThickness) {
        List<Line> lines = new ArrayList<>();
        double cellWidth = canvasWidth / (double) gridSize;
        double cellHeight = canvasHeight / (double) gridSize;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                Node currentNode = getNodeAt(r, c);
                if (currentNode == null) continue; 

                double cellX = c * cellWidth;
                double cellY = r * cellHeight;

                if (r < gridSize - 1) {
                    Node downNode = getNodeAt(r + 1, c);
                    if (downNode != null && !connections.contains(getConnectionKey(currentNode, downNode))) {
                        Line wall = new Line(cellX, cellY + cellHeight, cellX + cellWidth, cellY + cellHeight);
                        wall.setStrokeColor(Color.BLACK);
                        wall.setStrokeWidth(wallThickness);
                        lines.add(wall);
                    }
                }

                if (c < gridSize - 1) {
                    Node rightNode = getNodeAt(r, c + 1);
                     if (rightNode != null && !connections.contains(getConnectionKey(currentNode, rightNode))) {
                        Line wall = new Line(cellX + cellWidth, cellY, cellX + cellWidth, cellY + cellHeight);
                        wall.setStrokeColor(Color.BLACK);
                        wall.setStrokeWidth(wallThickness);
                        lines.add(wall);
                    }
                }
            }
        }   
        Line topWall = new Line(0, 0, canvasWidth-cellWidth, 0);
        Line leftWall = new Line(0, 0, 0, canvasHeight-cellHeight);
        Line bottomWall = new Line(cellWidth, canvasHeight, canvasWidth, canvasHeight);
        Line rightWall = new Line(canvasWidth, cellHeight, canvasWidth, canvasHeight);

        topWall.setStrokeColor(Color.BLACK);
        leftWall.setStrokeColor(Color.BLACK);
        bottomWall.setStrokeColor(Color.BLACK);
        rightWall.setStrokeColor(Color.BLACK);

        topWall.setStrokeWidth(wallThickness);
        leftWall.setStrokeWidth(wallThickness);
        bottomWall.setStrokeWidth(wallThickness);
        rightWall.setStrokeWidth(wallThickness);

        lines.add(topWall);
        lines.add(leftWall);
        lines.add(bottomWall);
        lines.add(rightWall);

        return lines;

    }
    
    /**
     * method to draw the maze on the canvas
     * @param canvas the canvas to draw on
     * @param lines the lines of the maze
     * @param canvasWidth the width of canvas to draw on
     * @param canvasHeight the height of canvas to draw on
     * @param wallThickness The desired stroke width for the wall lines.
     */
    public void drawMaze(CanvasWindow canvas, List<Line> lines, int canvasWidth, int canvasHeight, double wallThickness) {
        for (Line line : lines) {
            canvas.add(line);
        }
    }


    
    
    /**
     * method to get the nodes and edges of the maze
     * @return a list of nodes and edges
     */
    public List<Node> getNodes() {
        return nodes;
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
        public int row;
        public int col;

        Node(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    /**
     * class representing a edge in the maze
     * each node has a row and column position
     */
    public static class Edge {
        public Node nodeA;
        public Node nodeB;

        Edge(Node nodeA, Node nodeB) {
            this.nodeA = nodeA;
            this.nodeB = nodeB;
        }
    }
}
