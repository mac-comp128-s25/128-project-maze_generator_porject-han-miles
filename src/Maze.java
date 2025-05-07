import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Ellipse;
import edu.macalester.graphics.FontStyle;
import edu.macalester.graphics.GraphicsText;
import edu.macalester.graphics.Line;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.Polygon;
import edu.macalester.graphics.events.KeyboardEvent;
import edu.macalester.graphics.ui.Button;

public class Maze {
    private final CanvasWindow canvas;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final int WALL_THICKNESS = 30;
    private static final double PLAYER_RADIUS = 8.0;
    private static final double PLAYER_SPEED = 1.0;
    private static final int GRID_SIZE = 10;

    private List<Line> walls = new ArrayList<>();
    private Ellipse player;
    private double playerDX = 0; 
    private double playerDY = 0;

    private Point startCellCenter;
    private Point endCellCenter;
    private boolean gameStarted = false;
    private long startTimeMillis = 0;
    private GraphicsText messageText;
    private GraphicsText timerText;
    private Button startButton;
    private double cellWidth = (double) CANVAS_WIDTH / GRID_SIZE;
    private double cellHeight = (double) CANVAS_HEIGHT / GRID_SIZE;
    private long messageRemoveTime = -1;

    
    public Maze(){
        canvas = new CanvasWindow("Maze", 800, 600);
        /* generate a maze using Prim's algorithm */
        PrimsAlgorithmGenerator generator = new PrimsAlgorithmGenerator(GRID_SIZE); 
        generator.generateMaze();
        // generator.addRandomEdges(0.20);
        walls = generator.generateMazeLines(CANVAS_WIDTH, CANVAS_HEIGHT, WALL_THICKNESS);
        generator.drawMaze(canvas, walls, CANVAS_WIDTH, CANVAS_HEIGHT, WALL_THICKNESS);

        // /* generate a maze using Wilson's algorithm */
        // WilsonsAlgorithmGenerator generator = new WilsonsAlgorithmGenerator(GRID_SIZE); 
        // generator.generateMaze();
        // walls = generator.generateMazeLines(CANVAS_WIDTH, CANVAS_HEIGHT, WALL_THICKNESS);
        // generator.drawMaze(canvas, walls, CANVAS_WIDTH, CANVAS_HEIGHT, WALL_THICKNESS);
        
        startCellCenter = new Point(
            cellWidth * 0.5,
            cellHeight * (GRID_SIZE - 1) + cellHeight * 0.5);
        endCellCenter = new Point(
            cellWidth * (GRID_SIZE - 1) + cellWidth * 0.5,
            cellHeight * 0.5);
        player = new Ellipse(0, 0, PLAYER_RADIUS * 2, PLAYER_RADIUS * 2);
        player.setCenter(startCellCenter); // Start at the center of the bottom-left cell
        player.setFillColor(Color.BLUE);
        player.setStroked(false);

        messageText = new GraphicsText("", CANVAS_WIDTH / 2.0, CANVAS_HEIGHT * 0.1);
        messageText.setFont(FontStyle.BOLD, 20);
        messageText.setFillColor(Color.RED);
        messageText.setCenter(messageText.getCenter().getX(), messageText.getY());

        startButton = new Button("Start Game");
        startButton.setCenter(CANVAS_WIDTH / 2.0, CANVAS_HEIGHT / 2.0);
        canvas.add(startButton);

        initializePlayer();

        startButton.onClick(() -> startGame());
        canvas.onKeyDown(this::handleKeyPress);
        canvas.animate(this::updateGame);
        drawEndFlag();
    }

    // private List<Line> findWallsOnCanvas(CanvasWindow targetCanvas) {
    //     List<Line> foundWalls = new ArrayList<>();
    //     for (GraphicsObject obj : targetCanvas.getAllGraphicsObjects()) {
    //         if (obj instanceof Line) {
    //             foundWalls.add((Line) obj);
    //         }
    //     }
    //     return foundWalls;
    // }

    private void startGame() {
        if (gameStarted) {
            return;
        }
        gameStarted = true;
        startTimeMillis = System.currentTimeMillis();

        canvas.remove(startButton);
        canvas.remove(messageText);
        messageText.setText("");
        player.setCenter(startCellCenter);
        playerDX = 0;
        playerDY = 0;
    }

    private void initializePlayer() {
        double cellSize = CANVAS_WIDTH / (double) GRID_SIZE;
        double startX = cellSize / 2.0; 
        double startY = CANVAS_HEIGHT- cellSize / 3.0;
    
        player = new Ellipse(startX - PLAYER_RADIUS, startY - PLAYER_RADIUS, PLAYER_RADIUS * 2, PLAYER_RADIUS * 2);
        player.setFillColor(Color.BLUE);
    
        canvas.add(player);
    }

    private void handleKeyPress(KeyboardEvent event) {
        if (!gameStarted) {
            return;
        }
        playerDX = 0;
        playerDY = 0;

        if (null != event.getKey()) switch (event.getKey()) {
            case UP_ARROW:
            case W:
                playerDY = -PLAYER_SPEED;
                break;
            case DOWN_ARROW:
            case S:
                playerDY = PLAYER_SPEED;
                break;
            case LEFT_ARROW:
            case A:
                playerDX = -PLAYER_SPEED;
                break;
            case RIGHT_ARROW:
            case D:
                playerDX = PLAYER_SPEED;
                break;
            default:
                break;
        }
    }

    private void updateGame(double dt) {
         if (!gameStarted) {
            return;
        }
        if (messageRemoveTime > 0 && System.currentTimeMillis() >= messageRemoveTime) {
            if (messageText.getText().equals("This is a wall!")) { 
               canvas.remove(messageText);
               messageText.setText(""); 
            }
            messageRemoveTime = -1; 
        }
        if (player.getCenter().getX() < 0) {
            playerDX = 0;
            playerDY = 0;
        }
        if (player.getCenter().getX() > CANVAS_WIDTH) {
            playerDX = 0;
            playerDY = 0;
        }
        if (player.getCenter().getY() < 0) {
            playerDX = 0;
            playerDY = 0;
        }
        if (player.getCenter().getY() > CANVAS_HEIGHT) {
            playerDX = 0;
            playerDY = 0;
        }


        Point currentCenter = player.getCenter();
        double nextX = currentCenter.getX() + playerDX;
        double nextY = currentCenter.getY() + playerDY;

        boolean collision = checkCollision(nextX, nextY);

        if (collision) {
            playerDX = 0;
            playerDY = 0;
            if (!messageText.getText().equals("This is a wall!")) {
                showMessage("This is a wall!", Color.ORANGE, 1000); 
             }
        } else {
            player.setCenter(nextX, nextY);
            //  if (messageText.getText().equals("This is a wall!")) {
            //      messageText.setText("");
            //      canvas.remove(messageText);
            //  }
        }
        if (player.getCenter().distance(endCellCenter) < WALL_THICKNESS/2.0) {
            winGame();
        }
    }

    private boolean checkCollision(double nextX, double nextY) {
        for (Line wall : walls) {
            if (lineIntersectsCircle(wall, nextX, nextY, WALL_THICKNESS/2.0)) {
                return true;
            }
        }
        return false;
    }

    private boolean lineIntersectsCircle(Line line, double cx, double cy, double radius) {
        double x1 = line.getX1();
        double y1 = line.getY1();
        double x2 = line.getX2();
        double y2 = line.getY2();
    
        double dx = x2 - x1;
        double dy = y2 - y1;
        double lengthSquared = dx * dx + dy * dy;
        double t = ((cx - x1) * dx + (cy - y1) * dy) / lengthSquared;
        t = Math.max(0, Math.min(1, t)); 
    
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;
    
        double distanceSquared = (closestX - cx) * (closestX - cx) + (closestY - cy) * (closestY - cy);
        return distanceSquared <= 3*radius/2 * 3*radius/2;
    }


    private void showMessage(String text, Color color, long durationMs) {
       messageText.setText(text);
       messageText.setFillColor(color);
       messageText.setCenter(CANVAS_WIDTH / 2.0, CANVAS_HEIGHT * 0.1);
       if (canvas.getElementAt(messageText.getCenter()) != messageText) {
            canvas.add(messageText);
       }

       if (durationMs > 0) {
        messageRemoveTime = System.currentTimeMillis() + durationMs;
       } else{
        messageRemoveTime = -1;
       }
   }

   private void winGame() {
    gameStarted = false;
    playerDX = 0;
    playerDY = 0;

    long endTimeMillis = System.currentTimeMillis();
    double timeSeconds = (endTimeMillis - startTimeMillis) / 1000.0;

    String winMsg = String.format("Congratulations! You finished in %.2f seconds.", timeSeconds);
    showMessage(winMsg, Color.GREEN, 0);

    canvas.remove(player);
}

private void drawEndFlag() {
    double poleX = endCellCenter.getX();
    double poleBottomY = endCellCenter.getY() + cellHeight * 0.4; 
    double poleTopY = endCellCenter.getY() - cellHeight * 0.4; 
    double flagWidth = cellWidth * 0.4;
    double flagHeight = cellHeight * 0.3;

    Line pole = new Line(poleX, poleBottomY, poleX, poleTopY);
    pole.setStrokeColor(Color.BLACK);
    pole.setStrokeWidth(2); 
    canvas.add(pole);

    List<Point> flagPoints = List.of(
        new Point(poleX, poleTopY),                      
        new Point(poleX + flagWidth, poleTopY + flagHeight / 2.0),
        new Point(poleX, poleTopY + flagHeight)        
    );
    Polygon flagBody = new Polygon(flagPoints);
    flagBody.setFillColor(Color.RED);
    flagBody.setStroked(false); 
    canvas.add(flagBody);
}


    
    public static void main(String[] args) {
        new Maze();  
    }
}
