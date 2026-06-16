package backend;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import lombok.Getter;

public class PartialBlock extends GameEntity {

    public enum BlockDirection {
        TOP, BOTTOM, LEFT, RIGHT
    }

    @Getter
    private final BlockDirection blockDirection;

    public PartialBlock(int x, int y, int width, int height, BlockDirection blockDirection) {
        super(x, y, width, height);
        isWalkable = false;
        this.zIndex = 1;
        this.blockDirection = blockDirection;
    }
}