import edu.macalester.graphics.CanvasWindow;

public class Maze {
    private final CanvasWindow canvas;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int NODE_RADIUS = 30;
    public Maze(){
        canvas = new CanvasWindow("Maze", 800, 600);
        /* generate a maze using Prim's algorithm */
        int gridSize = 10;
        PrimsAlgorithmGenerator generator = new PrimsAlgorithmGenerator(gridSize); 
        generator.generateMaze();
        generator.addRandomEdges(0.05);
        generator.drawMaze(canvas, CANVAS_WIDTH, CANVAS_HEIGHT, NODE_RADIUS);
    }

    
    public static void main(String[] args) {
        new Maze();  
    }
}
