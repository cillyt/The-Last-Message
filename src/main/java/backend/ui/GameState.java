package backend.ui;

import javafx.scene.canvas.GraphicsContext;

public interface GameState {
    void enter();
    void update(double deltaTime);
    void render(GraphicsContext gc, int width, int height);
    void exit();
}

