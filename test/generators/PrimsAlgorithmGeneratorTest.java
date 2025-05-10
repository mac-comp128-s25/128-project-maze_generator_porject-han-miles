package generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generators.PrimsAlgorithmGenerator.Node;
import generators.PrimsAlgorithmGenerator.Edge;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import edu.macalester.graphics.Line;
import generators.PrimsAlgorithmGenerator;

public class PrimsAlgorithmGeneratorTest {

    private PrimsAlgorithmGenerator generator;
    private final int DEFAULT_GRID_SIZE = 5;

    @BeforeEach
    void setUp() {
        generator = new PrimsAlgorithmGenerator(DEFAULT_GRID_SIZE);
    }

    @Test
    void initializesNodesCorrectly() {
        List<Node> nodes = generator.getNodes();
        assertNotNull(nodes, "Nodes list should not be null.");
        assertEquals(DEFAULT_GRID_SIZE * DEFAULT_GRID_SIZE, nodes.size(),
                     "Number of nodes should be gridSize * gridSize.");

        // Check if nodes are created for each cell
        Set<String> expectedNodeCoords = new HashSet<>();
        for (int r = 0; r < DEFAULT_GRID_SIZE; r++) {
            for (int c = 0; c < DEFAULT_GRID_SIZE; c++) {
                expectedNodeCoords.add(r + "_" + c);
            }
        }

        Set<String> actualNodeCoords = new HashSet<>();
        for (Node node : nodes) {
            actualNodeCoords.add(node.row + "_" + node.col);
        }
        assertEquals(expectedNodeCoords, actualNodeCoords, "All cell coordinates should have a corresponding node.");
    }

    @Test
    void producesCorrectNumberOfEdgesForSpanningTree() {
        generator.generateMaze();
        List<Edge> edges = generator.getEdges();
        List<Node> nodes = generator.getNodes();

        assertNotNull(edges, "Edges list should not be null after generation.");
        if (nodes.size() > 0) {
            assertEquals(nodes.size() - 1, edges.size(),
                         "A spanning tree should have V-1 edges.");
        } else {
            assertEquals(0, edges.size(), "No edges for an empty grid.");
        }
    }

    @Test
    void allNodesAreConnected() {
        generator.generateMaze();
        List<Node> nodes = generator.getNodes();
        List<Edge> edges = generator.getEdges();

        if (nodes.isEmpty()) {
            assertTrue(true, "Empty grid, vacuously connected.");
            return;
        }

        Set<Node> visited = new HashSet<>();
        List<Node> queue = new ArrayList<>();

        Node startNode = nodes.get(0);
        queue.add(startNode);
        visited.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.remove(0);
            for (Edge edge : edges) {
                Node neighbor = null;
                if (edge.nodeA == current && !visited.contains(edge.nodeB)) {
                    neighbor = edge.nodeB;
                } else if (edge.nodeB == current && !visited.contains(edge.nodeA)) {
                    neighbor = edge.nodeA;
                }

                if (neighbor != null) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        assertEquals(nodes.size(), visited.size(),
                     "All nodes should be reachable (connected) after maze generation.");
    }

    @Test
    void noCyclesinMaze() {
        generator.generateMaze();
        List<Node> nodes = generator.getNodes();
        List<Edge> edges = generator.getEdges();

        if (nodes.size() > 0) {
            assertTrue(edges.size() == nodes.size() - 1,
                       "A perfect maze should have V-1 edges, indicating no cycles if connected.");
        }
    }


    @Test
    void addsEdgesWithGivenProbability() {
        generator.generateMaze(); 
        int initialEdgeCount = generator.getEdges().size();

        // Test with 0 probability
        PrimsAlgorithmGenerator genZeroProb = new PrimsAlgorithmGenerator(DEFAULT_GRID_SIZE);
        genZeroProb.generateMaze();
        int initialEdgesZeroProb = genZeroProb.getEdges().size();
        genZeroProb.addRandomEdges(0.0);
        assertEquals(initialEdgesZeroProb, genZeroProb.getEdges().size(),
                     "Adding random edges with 0 probability should not add any edges.");

        PrimsAlgorithmGenerator genFullProb = new PrimsAlgorithmGenerator(DEFAULT_GRID_SIZE);
        genFullProb.generateMaze(); 
        int maxPossibleEdges = DEFAULT_GRID_SIZE * (DEFAULT_GRID_SIZE - 1) + (DEFAULT_GRID_SIZE - 1) * DEFAULT_GRID_SIZE;

        genFullProb.addRandomEdges(1.0);
        List<Node> nodes = genFullProb.getNodes();
        int totalPotentialConnections = 0;
        for (Node node : nodes) {
            totalPotentialConnections += genFullProb.getPotentialNeighbors(node).size();
        }
        totalPotentialConnections /= 2;

        assertEquals(totalPotentialConnections, genFullProb.getEdges().size(),
                     "Adding random edges with 1.0 probability should connect all possible neighbors.");
    }

    @Test
    void generatesOuterBoundaryWalls() {
        PrimsAlgorithmGenerator smallGen = new PrimsAlgorithmGenerator(1);
        smallGen.generateMaze(); 
        List<Line> lines = smallGen.generateMazeLines(100, 100, 2);
        PrimsAlgorithmGenerator gen2x2 = new PrimsAlgorithmGenerator(2);
        gen2x2.generateMaze(); 
        List<Line> lines2x2 = gen2x2.generateMazeLines(200, 200, 2); // cellWidth/Height = 100

        boolean foundTop = lines2x2.stream().anyMatch(l -> l.getX1()==0 && l.getY1()==0 && l.getX2()==100 && l.getY2()==0);
        boolean foundLeft = lines2x2.stream().anyMatch(l -> l.getX1()==0 && l.getY1()==0 && l.getX2()==0 && l.getY2()==100);
        boolean foundBottom = lines2x2.stream().anyMatch(l -> l.getX1()==100 && l.getY1()==200 && l.getX2()==200 && l.getY2()==200);
        boolean foundRight = lines2x2.stream().anyMatch(l -> l.getX1()==200 && l.getY1()==100 && l.getX2()==200 && l.getY2()==200);

        assertTrue(foundTop, "Top boundary wall segment missing.");
        assertTrue(foundLeft, "Left boundary wall segment missing.");
        assertTrue(foundBottom, "Bottom boundary wall segment missing.");
        assertTrue(foundRight, "Right boundary wall segment missing.");
        assertTrue(lines2x2.size() >= 5, "Should have boundary walls and at least one internal wall for 2x2 grid.");
    }

    @Test
    void handleEmptyGrid() {
        PrimsAlgorithmGenerator emptyGen = new PrimsAlgorithmGenerator(0);
        assertDoesNotThrow(() -> emptyGen.generateMaze());
        assertEquals(0, emptyGen.getNodes().size());
        assertEquals(0, emptyGen.getEdges().size());
        List<Line> lines = emptyGen.generateMazeLines(100, 100, 2);
        assertDoesNotThrow(() -> emptyGen.generateMazeLines(100, 100, 2));
    }

}