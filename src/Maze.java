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
        Map<PrimsAlgorithmGenerator.Node, double[]> nodePositions = generateNodePositions(generator, gridSize);
        drawMaze(generator, nodePositions);
    }

    private Map<PrimsAlgorithmGenerator.Node, double[]> generateNodePositions(PrimsAlgorithmGenerator generator, int gridSize) {
        Map<PrimsAlgorithmGenerator.Node, double[]> positions = new HashMap<>();
        double cellWidth = CANVAS_WIDTH / (double) gridSize;
        double cellHeight = CANVAS_HEIGHT / (double) gridSize;

        for (PrimsAlgorithmGenerator.Node node : generator.getNodes()) {
            double x = node.col * cellWidth + cellWidth / 2;
            double y = node.row * cellHeight + cellHeight / 2;
            positions.put(node, new double[]{x, y});
        }
        return positions;
    }

    private void drawMaze(PrimsAlgorithmGenerator generator, Map<PrimsAlgorithmGenerator.Node, double[]> nodePositions) {
        for (PrimsAlgorithmGenerator.Edge edge : generator.getEdges()) {
            double[] posA = nodePositions.get(edge.nodeA);
            double[] posB = nodePositions.get(edge.nodeB);

            Line line = new Line(posA[0], posA[1], posB[0], posB[1]);
            line.setStrokeColor(Color.BLACK);
            canvas.add(line);
        }
        for (Map.Entry<PrimsAlgorithmGenerator.Node, double[]> entry : nodePositions.entrySet()) {
            double[] pos = entry.getValue();
            Ellipse circle = new Ellipse(pos[0] - NODE_RADIUS, pos[1] - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            circle.setFillColor(Color.BLUE);
            canvas.add(circle);
        }
    }
    public static void main(String[] args) {
        new Maze();  
    }
}
