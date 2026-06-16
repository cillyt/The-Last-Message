package backend.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;

public interface GameState {
    void enter();
    void update(double deltaTime);
    void render(GraphicsContext gc, int width, int height);
    void onKeyPressed(KeyEvent event);
    void exit();
}
