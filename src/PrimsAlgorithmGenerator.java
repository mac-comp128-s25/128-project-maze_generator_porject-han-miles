import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Ellipse;
import edu.macalester.graphics.Line;


public class PrimsAlgorithmGenerator {
    private final List<Node> nodes;
    private final List<Edge> edges;
    private final Random random;
    private final int gridSize;
    /**
    * constractor
    * @param gridSize the size of the grid
    */
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

    /**
    * method to generate a maze using Prim's algorithm with a random starting point
    */
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

    /**
     * method to get a random neighbor of a node from the outside list
     * @param node the node to get a neighbor for
     * @param outside the list of nodes outside the maze
     * @return a random neighbor node or null if no neighbors are found
     */
    private Node getRandomNeighbor(Node node, List<Node> outside) {
        List<Node> neighbors = new ArrayList<>();
        for (Node potentialNeighbor : outside) {
            if (isNeighbor(node, potentialNeighbor)) {
                neighbors.add(potentialNeighbor);
            }
        }
        if (neighbors.isEmpty()) {
            return null;
        }
        return neighbors.get(random.nextInt(neighbors.size()));
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
        for (Node node : nodes) {
            for (Node neighbor : getPotentialNeighbors(node)) {
                if (!areConnected(node, neighbor) && random.nextDouble() < probability) {
                    edges.add(new Edge(node, neighbor));
                }
            }
        }
    }
    
    /**
     * method to get potential neighbors of a node
     * @param node the node to get neighbors for
     * @return a list of potential neighbors
     */
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
    

    /**
     * method to get a node at a specific position
     * @param row the row of the node
     * @param col the column of the node
     * @return the node at the specified position
     */
    private Node getNodeAt(int row, int col) {
        return nodes.get(row * gridSize + col);
    }

    /**
     * method to check if two nodes are connected
     * @param nodeA the first node
     * @param nodeB the second node
     * @return true if they are connected, false otherwise
     */
    private boolean areConnected(Node nodeA, Node nodeB) {
        for (Edge edge : edges) {
            if ((edge.nodeA == nodeA && edge.nodeB == nodeB) ||
                (edge.nodeA == nodeB && edge.nodeB == nodeA)) {
                return true;
            }
        }
        return false;
    }

    /**
     * method to generate node positions for drawing
     * @param canvasWidth the width of the canvas
     * @param canvasHeight the height of the canvas
     * @return a map of nodes to their positions
     */
    public Map<Node, double[]> generateNodePositions(int canvasWidth, int canvasHeight) {
        Map<Node, double[]> positions = new HashMap<>();
        double cellWidth = canvasWidth / (double) gridSize;
        double cellHeight = canvasHeight / (double) gridSize;

        for (Node node : nodes) {
            double x = node.col * cellWidth + cellWidth / 2;
            double y = node.row * cellHeight + cellHeight / 2;
            positions.put(node, new double[]{x, y});
        }
        return positions;
    }


    /**
     * method to draw the maze on a canvas
     * @param canvas the canvas to draw on
     * @param nodePositions the positions of the nodes
     * @param nodeRadius the radius of the nodes
     */
    public void drawMaze(CanvasWindow canvas, Map<Node, double[]> nodePositions, int nodeRadius) {
        for (Edge edge : edges) {
            double[] posA = nodePositions.get(edge.nodeA);
            double[] posB = nodePositions.get(edge.nodeB);

            Line line = new Line(posA[0], posA[1], posB[0], posB[1]);
            line.setStrokeColor(Color.BLACK);
            canvas.add(line);
        }

        for (Map.Entry<Node, double[]> entry : nodePositions.entrySet()) {
            double[] pos = entry.getValue();
            Ellipse circle = new Ellipse(pos[0] - nodeRadius, pos[1] - nodeRadius, nodeRadius * 2, nodeRadius * 2);
            circle.setFillColor(Color.BLUE);
            canvas.add(circle);
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
        int row, col;

        Node(int row, int col) {
            this.row = row;
            this.col = col;
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
