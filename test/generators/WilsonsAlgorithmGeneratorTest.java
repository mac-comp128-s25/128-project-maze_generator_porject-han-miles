package generators; // Assuming it's in this package

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest; // For tests involving randomness

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import generators.WilsonsAlgorithmGenerator;
import generators.WilsonsAlgorithmGenerator.Cell;
import generators.WilsonsAlgorithmGenerator.Direction; 
import edu.macalester.graphics.Line; 

//The loopErasedRandomWalkReachesVisitedCell test can't be run individually.
public class WilsonsAlgorithmGeneratorTest {

    private WilsonsAlgorithmGenerator generator;
    private final int DEFAULT_GRID_SIZE = 5; 
    private final int LARGER_GRID_SIZE = 10; 

    @BeforeEach
    void setUp() {
        generator = new WilsonsAlgorithmGenerator(DEFAULT_GRID_SIZE);
    }

    @Test
    void initializesGridWithCells() {
        WilsonsAlgorithmGenerator gen = new WilsonsAlgorithmGenerator(2);
        Cell[][] maze = gen.generateMaze(); 
        assertNotNull(maze, "Generated maze grid should not be null.");
        assertEquals(2, maze.length, "Grid should have correct number of rows.");
        if (maze.length > 0) {
            assertEquals(2, maze[0].length, "Grid should have correct number of columns.");
        }
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                assertNotNull(maze[r][c], "Cell at (" + r + "," + c + ") should not be null.");
                assertEquals(r, maze[r][c].row);
                assertEquals(c, maze[r][c].col);
                }
        }
    }

    @Test
    void allCellsAreVisited() {
        Cell[][] maze = generator.generateMaze();
        for (int r = 0; r < DEFAULT_GRID_SIZE; r++) {
            for (int c = 0; c < DEFAULT_GRID_SIZE; c++) {
                assertTrue(maze[r][c].visited,
                           "Cell (" + r + "," + c + ") should be visited after maze generation.");
            }
        }
    }

    @RepeatedTest(5) 
    void createsSpanningTreeProperties() {
        WilsonsAlgorithmGenerator localGen = new WilsonsAlgorithmGenerator(LARGER_GRID_SIZE);
        Cell[][] maze = localGen.generateMaze();

        int numNodes = LARGER_GRID_SIZE * LARGER_GRID_SIZE;
        if (numNodes == 0) return;

        int numEdges = 0;
        for (int r = 0; r < LARGER_GRID_SIZE; r++) {
            for (int c = 0; c < LARGER_GRID_SIZE; c++) {
                Cell current = maze[r][c];
                if (!current.eastWall && c < LARGER_GRID_SIZE - 1) {
                    numEdges++; // Count edges by checking open east walls (avoids double counting)
                }
                if (!current.southWall && r < LARGER_GRID_SIZE - 1) {
                    numEdges++; // Count edges by checking open south walls
                }
            }
        }
        assertEquals(numNodes - 1, numEdges,
                     "A spanning tree (perfect maze) should have V-1 edges.");

        Set<Cell> visitedCellsInTraversal = new HashSet<>();
        List<Cell> queue = new ArrayList<>();

        Cell startCell = maze[0][0];
        queue.add(startCell);
        visitedCellsInTraversal.add(startCell);

        while (!queue.isEmpty()) {
            Cell current = queue.remove(0);
            // Check North
            if (!current.northWall && current.row > 0) {
                Cell neighbor = maze[current.row - 1][current.col];
                if (!visitedCellsInTraversal.contains(neighbor)) {
                    visitedCellsInTraversal.add(neighbor);
                    queue.add(neighbor);
                }
            }
            // Check South
            if (!current.southWall && current.row < LARGER_GRID_SIZE - 1) {
                Cell neighbor = maze[current.row + 1][current.col];
                if (!visitedCellsInTraversal.contains(neighbor)) {
                    visitedCellsInTraversal.add(neighbor);
                    queue.add(neighbor);
                }
            }
            // Check West
            if (!current.westWall && current.col > 0) {
                Cell neighbor = maze[current.row][current.col - 1];
                if (!visitedCellsInTraversal.contains(neighbor)) {
                    visitedCellsInTraversal.add(neighbor);
                    queue.add(neighbor);
                }
            }
            // Check East
            if (!current.eastWall && current.col < LARGER_GRID_SIZE - 1) {
                Cell neighbor = maze[current.row][current.col + 1];
                if (!visitedCellsInTraversal.contains(neighbor)) {
                    visitedCellsInTraversal.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        assertEquals(numNodes, visitedCellsInTraversal.size(),
                     "All cells should be connected in the generated maze.");
    }


    @Test
    void handleEmptyGrid() {
        WilsonsAlgorithmGenerator emptyGen = new WilsonsAlgorithmGenerator(0);
        Cell[][] maze = assertDoesNotThrow(() -> emptyGen.generateMaze());
        assertEquals(0, maze.length, "Maze grid for gridSize 0 should have 0 rows.");
        List<Line> lines = assertDoesNotThrow(() -> emptyGen.generateMazeLines(100,100,2));
        assertNotNull(lines, "generateMazeLines should not return null for gridSize 0.");
    }

    @Test
    void handleSingleCellGrid() {
        WilsonsAlgorithmGenerator singleCellGen = new WilsonsAlgorithmGenerator(1);
        Cell[][] maze = assertDoesNotThrow(() -> singleCellGen.generateMaze());
        assertEquals(1, maze.length);
        assertEquals(1, maze[0].length);
        assertTrue(maze[0][0].visited, "Single cell should be visited.");
        assertTrue(maze[0][0].northWall && maze[0][0].southWall && maze[0][0].eastWall && maze[0][0].westWall,
                   "Single cell should retain all its walls if interpreted as internal walls.");

        List<Line> lines = singleCellGen.generateMazeLines(100, 100, 2);
        assertEquals(2 + 4, lines.size(), "1x1 grid should have 2 internal walls drawn + 4 boundary lines.");
    }

    @Test
    void loopErasedRandomWalkReachesVisitedCell() {
        // Will have errors if run the test individually since some necessary set up are outside this test.
        WilsonsAlgorithmGenerator gen = new WilsonsAlgorithmGenerator(2);
        gen.grid[0][0].visited = true; 
        gen.remainingCells--;

        Cell startWalk = gen.grid[0][1]; 
        assertDoesNotThrow(() -> gen.generateMaze(), "Maze generation should complete even with pre-visited cell.");
        assertTrue(gen.grid[0][1].visited, "Cell from which walk started should become visited.");
        assertFalse(gen.grid[0][0].eastWall || gen.grid[0][1].westWall, 
                    "Wall between startWalk and initially visited cell should be carved.");
    }


    @Test
    void generatesCorrectInternalWallsBasedOnCellState() {
        WilsonsAlgorithmGenerator gen = new WilsonsAlgorithmGenerator(2);
        Cell[][] G = gen.generateMaze(); 
        List<Line> lines = gen.generateMazeLines(200, 200, 2);
        double cellWidth = 100;
        double cellHeight = 100;

        int expectedInternalLines = 0;
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 2; c++) {
                Cell current = G[r][c];
                double cellX = c * cellWidth;
                double cellY = r * cellHeight;

                if (current.southWall) { // This wall should be present if true
                    expectedInternalLines++;
                    final int R=r, C=c; // For lambda
                    assertTrue(lines.stream().anyMatch(l ->
                        l.getX1() == C * cellWidth && l.getY1() == (R + 1) * cellHeight &&
                        l.getX2() == (C + 1) * cellWidth && l.getY2() == (R + 1) * cellHeight),
                        "Missing south wall for cell (" + r + "," + c + ")");
                }
                if (current.eastWall) { // This wall should be present if true
                    expectedInternalLines++;
                    final int R=r, C=c; // For lambda
                    assertTrue(lines.stream().anyMatch(l ->
                        l.getX1() == (C + 1) * cellWidth && l.getY1() == R * cellHeight &&
                        l.getX2() == (C + 1) * cellWidth && l.getY2() == (R + 1) * cellHeight),
                        "Missing east wall for cell (" + r + "," + c + ")");
                }
            }
        }
        assertEquals(expectedInternalLines + 4, lines.size(), "Mismatch in total number of lines generated.");
    }

    @Test
    void directionOppositeWorks() {
        assertEquals(Direction.SOUTH, Direction.NORTH.opposite());
        assertEquals(Direction.NORTH, Direction.SOUTH.opposite());
        assertEquals(Direction.WEST, Direction.EAST.opposite());
        assertEquals(Direction.EAST, Direction.WEST.opposite());
    }
}
