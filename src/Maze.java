import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Ellipse;
import edu.macalester.graphics.Line;

public class Maze {
    private CanvasWindow canvas;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int NODE_RADIUS = 10;
    public Maze(){
        canvas = new CanvasWindow("Maze", 800, 600);
        int gridSize = 10;
        PrimsAlgorithmGenerator generator = new PrimsAlgorithmGenerator(gridSize); 
        generator.generateMaze();
        generator.addRandomEdges(0.15);
        var nodePositions = generator.generateNodePositions(CANVAS_WIDTH, CANVAS_HEIGHT);
        generator.drawMaze(canvas, nodePositions, NODE_RADIUS);
    }

    
    public static void main(String[] args) {
        new Maze();  
    }
}
