package backend;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BlockOfGround extends GameEntity {

    public BlockOfGround(int x, int y, int width, int height) {
        super(x, y, width, height, false);
        isWalkable = false;
        this.zIndex = 1;
    }

    @Override
    public void render(GraphicsContext gc) {
        CameraWindow camera = CameraWindow.getInstance();

        boolean isVisible = (x + width > camera.getX()) &&
                (x < camera.getX() + camera.getScreenWidth()) &&
                (y + height > camera.getY()) &&
                (y < camera.getY() + camera.getScreenHeight());

        this.inCamera = isVisible;

        if (isVisible) {
            double screenX = this.x - camera.getExactX();
            double screenY = this.y - camera.getExactY();

            gc.setFill(Color.web("#6b7785"));
            gc.fillRect(screenX, screenY, width, height);

            gc.setStroke(Color.web("#2a2e33"));
            gc.setLineWidth(4);
            gc.strokeRect(screenX + 2, screenY + 2, width - 4, height - 4);
        }
    }
}