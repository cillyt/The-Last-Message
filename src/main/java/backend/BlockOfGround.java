package backend;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BlockOfGround extends GameEntity {

    public BlockOfGround(int x, int y, int width, int height) {
        super(x, y, width, height, false);
        isWalkable = false;
        this.zIndex = 1;
    }
}