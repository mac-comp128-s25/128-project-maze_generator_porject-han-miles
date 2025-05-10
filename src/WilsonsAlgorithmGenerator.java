import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Line;



public class WilsonsAlgorithmGenerator {
    private final Cell[][] grid;
    private final int gridSize;
    private int remainingCells;
    private final Random random = new Random();

    /**
    * constractor
    * @param gridSize the size of the grid
    */
    public WilsonsAlgorithmGenerator(int gridSize){
        this.gridSize = gridSize;
        this.grid = new Cell[gridSize][gridSize];
        this.remainingCells = gridSize * gridSize;
        initializeGrid();
    }

    /**
     * Initializes the grid with Cell objects.
     * Each cell is represented by a Cell object with its row and column indices.
     */
    private void initializeGrid() {
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
    }


    /**
     * Helper to find the first unvisited cell (left-to-right, top-to-bottom)
     * @return the first unvisited cell
     */ 
    private Cell chooseUnvisitedCell() {
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                if (!grid[r][c].visited) {
                    return grid[r][c];
                }
            }
        }
        return null; 
    }
    
    /**
     * Generates a maze using Wilson's algorithm.
     * The algorithm starts from a random cell and performs loop-erased random walks
     * until all cells are visited.
     *
     * @return A 2D array of Cell objects representing the generated maze.
     */
    public Cell[][] generateMaze() {
        if (gridSize == 0) return grid; 
        int startR = random.nextInt(gridSize);
        int startC = random.nextInt(gridSize);
        grid[startR][startC].visited = true;
        remainingCells--;
        if (remainingCells == 0) return grid; 

        while (remainingCells > 0) {
            Cell currentWalkStartCell = chooseUnvisitedCell();
            if (currentWalkStartCell == null) {
                 System.err.println("Error: No unvisited cell found, but remainingCells = " + remainingCells);
                 break; 
            }

            List<Cell> path = performLoopErasedRandomWalk(currentWalkStartCell);

            carvePath(path);
        }
        return grid;
    }


    /**
     * Performs a loop-erased random walk starting from the given cell.
     * The walk continues until it either returns to a visited cell or
     * reaches a dead end. The path is then returned as a list of cells.
     *
     * @param startCell The starting cell for the random walk.
     * @return A list of cells representing the path of the random walk.
     */
    private List<Cell> performLoopErasedRandomWalk(Cell startCell) {
        List<Cell> currentPath = new ArrayList<>();
        Map<Cell, Integer> pathIndices = new HashMap<>(); 
        Cell current = startCell;

        while (!current.visited) {
            Integer existingIndex = pathIndices.get(current); 

            if (existingIndex != null) { 
                while (currentPath.size() > existingIndex + 1) {
                    Cell removed = currentPath.remove(currentPath.size() - 1);
                    pathIndices.remove(removed);
                }
                current = currentPath.get(existingIndex);
            } else {
                pathIndices.put(current, currentPath.size());
                currentPath.add(current);
            }

            List<Direction> possibleDirections = getValidDirections(current);
            if (possibleDirections.isEmpty()) {
                 break;
            }
            Direction chosenDir = possibleDirections.get(random.nextInt(possibleDirections.size()));
            current = getNeighbor(current, chosenDir); 
            if (current == null) {
                throw new IllegalStateException("Moved to an invalid neighbor.");
            }
        }
        currentPath.add(current);
        return currentPath;
    }


    /**
     * Carves the path through the maze by removing walls between adjacent cells.
     * The path is represented as a list of cells, and the walls are removed
     * between each pair of adjacent cells in the path.
     *
     * @param fullPathIncludingHitCell The list of cells representing the path.
     */
    private void carvePath(List<Cell> fullPathIncludingHitCell) {
        if (fullPathIncludingHitCell.size() < 2) {
             if (!fullPathIncludingHitCell.isEmpty()) {
                 Cell cell = fullPathIncludingHitCell.get(0);
                 if (!cell.visited) {
                     cell.visited = true;
                     remainingCells--;
                 }
             }
             if(fullPathIncludingHitCell.size() == 2) {
                 Cell start = fullPathIncludingHitCell.get(0);
                 Cell hit = fullPathIncludingHitCell.get(1);
                 removeWall(start, hit);
                 if (!start.visited) { 
                      start.visited = true;
                      remainingCells--;
                 }
                 return; 
             } else if (fullPathIncludingHitCell.size() < 2) {
                 return; 
             }
        }

        for (int i = 0; i < fullPathIncludingHitCell.size() - 1; i++) {
            Cell current = fullPathIncludingHitCell.get(i);
            Cell next = fullPathIncludingHitCell.get(i + 1); 
            removeWall(current, next);
            if (!current.visited) {
                current.visited = true;
                remainingCells--;
            }
        }
    }

    /**
     * Helper to get valid directions (staying within bounds)
     * @param cell The cell to check for valid directions.
     * @return A list of valid directions from the given cell.
     */
    private List<Direction> getValidDirections(Cell cell) {
        List<Direction> directions = new ArrayList<>(4);
        if (cell.row > 0) directions.add(Direction.NORTH);
        if (cell.row < gridSize - 1) directions.add(Direction.SOUTH);
        if (cell.col > 0) directions.add(Direction.WEST);
        if (cell.col < gridSize - 1) directions.add(Direction.EAST);
        return directions;
    }

    /**
     * Helper to get the neighbor cell in a given direction.
     * @param cell The cell to check for neighbors.
     * @param dir The direction to check.
     * @return The neighboring cell in the specified direction, or null if out of bounds.
     */
    private Cell getNeighbor(Cell cell, Direction dir) {
        int nr = cell.row + dir.dr;
        int nc = cell.col + dir.dc;
        if (nr >= 0 && nr < gridSize && nc >= 0 && nc < gridSize) {
            return grid[nr][nc];
        }
        return null;
    }

    /**
     * Helper to get the direction between two cells.
     * @param c1 The first cell.
     * @param c2 The second cell.
     * @return The direction from c1 to c2, or null if they are the same cell.
     */
    private Direction getDirection(Cell c1, Cell c2) {
        if (c2.row < c1.row) return Direction.NORTH;
        if (c2.row > c1.row) return Direction.SOUTH;
        if (c2.col < c1.col) return Direction.WEST;
        if (c2.col > c1.col) return Direction.EAST;
        return null; 
    }

    /**
     * Helper to remove the wall between two adjacent cells.
     * @param c1
     * @param c2
     */
    private void removeWall(Cell c1, Cell c2) {
        if (c1 == null || c2 == null) return;

        if (c1.row == c2.row) {
            if (c1.col < c2.col) { 
                c1.eastWall = false;
                c2.westWall = false;
            } else { 
                c1.westWall = false;
                c2.eastWall = false;
            }
        } else {
            if (c1.row < c2.row) { 
                c1.southWall = false;
                c2.northWall = false;
            } else { 
                c1.northWall = false;
                c2.southWall = false;
            }
        }
    }


    /**
     * Generates a List of Line objects representing the walls of the generated maze.
     * This method iterates through the grid and creates lines based on the
     * boolean wall flags in each Cell. It draws boundaries explicitly.
     *
     * @param canvasWidth     The desired width of the canvas for scaling.
     * @param canvasHeight    The desired height of the canvas for scaling.
     * @param wallThickness   The desired stroke width for the wall lines.
     * @return A List of Line objects representing the maze walls.
     */
    public List<Line> generateMazeLines(int canvasWidth, int canvasHeight, double wallThickness) {
        List<Line> lines = new ArrayList<>();

        double cellWidth = (double) canvasWidth / gridSize;
        double cellHeight = (double) canvasHeight / gridSize;

        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                Cell currentCell = grid[r][c];
                double cellX = c * cellWidth;
                double cellY = r * cellHeight;

                if (currentCell.southWall) {
                    Line wall = new Line(cellX, cellY + cellHeight, cellX + cellWidth, cellY + cellHeight);
                    wall.setStrokeColor(Color.BLACK);
                    wall.setStrokeWidth(wallThickness);
                    lines.add(wall);
                }

                if (currentCell.eastWall) {
                    Line wall = new Line(cellX + cellWidth, cellY, cellX + cellWidth, cellY + cellHeight);
                    wall.setStrokeColor(Color.BLACK);
                    wall.setStrokeWidth(wallThickness);
                    lines.add(wall);
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
     * Adds a pre-generated list of maze wall lines to the specified CanvasWindow.
     *
     * @param canvas         The CanvasWindow to draw on.
     * @param lines          The List of Line objects representing the maze walls.
     * @param canvasWidth    
     * @param canvasHeight   
     * @param wallThickness  
     */
    public void drawMaze(CanvasWindow canvas, List<Line> lines, int canvasWidth, int canvasHeight, double wallThickness) {
        for (Line line : lines) {
            canvas.add(line);
        }
    }



    /**
     * Cell class representing a cell in the maze grid.
     * Each cell has a row and column index, wall flags, and a visited status.
     */
    public static class Cell {
        final int row;
        final int col;
        boolean visited = false; 

        boolean northWall = true;
        boolean southWall = true;
        boolean eastWall = true;
        boolean westWall = true;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return row == cell.row && col == cell.col;
        }

        public int hashCode() {
            return Objects.hash(row, col);
        }

        public String toString() {
            return "(" + row + "," + col + ")";
        }
    }

    /**
     * Enum representing the four possible directions (N, S, E, W) in the maze.
     * Each direction has a change in row and column associated with it.
     */
    enum Direction {
        NORTH(-1, 0),
        SOUTH(1, 0),
        EAST(0, 1),
        WEST(0, -1);
    
        final int dr; // Change in row
        final int dc; // Change in col
    
        Direction(int dr, int dc) {
            this.dr = dr;
            this.dc = dc;
        }
    
        public Direction opposite() {
            switch (this) {
                case NORTH: return SOUTH;
                case SOUTH: return NORTH;
                case EAST:  return WEST;
                case WEST:  return EAST;
                default:    throw new IllegalStateException("Unexpected value: " + this);
            }
        }
    }

}
