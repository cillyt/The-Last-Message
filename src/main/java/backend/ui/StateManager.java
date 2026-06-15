package backend.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

public class StateManager {
    private GameState currentState;
    private final StackPane rootPane;
    private final Canvas canvas;
    private final int width;
    private final int height;

    public StateManager(StackPane rootPane, Canvas canvas, int width, int height) {
        this.rootPane = rootPane;
        this.canvas = canvas;
        this.width = width;
        this.height = height;
    }

    public void changeState(GameState newState) {
        if (currentState != null) {
            currentState.exit();
        }
        currentState = newState;
        if (currentState != null) {
            currentState.enter();
        }
    }

    public void update(double deltaTime) {
        if (currentState != null) {
            currentState.update(deltaTime);
        }
    }

    public void render(GraphicsContext gc) {
        if (currentState != null) {
            currentState.render(gc, width, height);
        }
    }

    public StackPane getRootPane() { return rootPane; }
    public Canvas getCanvas() { return canvas; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

