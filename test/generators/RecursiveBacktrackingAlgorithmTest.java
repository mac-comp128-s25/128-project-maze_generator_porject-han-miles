package generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import generators.RecursiveBacktrackingAlgorithmGenerator.Node;
import generators.RecursiveBacktrackingAlgorithmGenerator.Edge;
import generators.RecursiveBacktrackingAlgorithmGenerator;

public class RecursiveBacktrackingAlgorithmTest {
    private RecursiveBacktrackingAlgorithmGenerator generator;
    private int size = 10;
    @BeforeEach
    void setUp() {
        generator = new RecursiveBacktrackingAlgorithmGenerator(size);
    }

     @Test
    void initializesNodesCorrectly() {
        List<Node> nodes = generator.getNodes();
        assertNotNull(nodes, "Nodes list should not be null.");
        assertEquals(size * size, nodes.size(),
                     "Number of nodes should be gridSize * gridSize.");

        // Check if nodes are created for each cell
        Set<String> expectedNodeCoords = new HashSet<>();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                expectedNodeCoords.add(x + "_" + y);
            }
        }

        Set<String> actualNodeCoords = new HashSet<>();
        for (Node node : nodes) {
            actualNodeCoords.add(node.x + "_" + node.y);
        }
        assertEquals(expectedNodeCoords, actualNodeCoords, "All cell coordinates should have a corresponding node.");
    }
    
    @Test
    void producesCorrectNumberOfEdgesForSpanningTree() {
        generator.generateMaze(null);
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
}
