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

        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();

        double startX = 0, startY = 0, endX = 0, endY = 0;

        switch (blockDirection) {
            case TOP -> { startY = 0; endY = 1; startX = 0; endX = 0; }
            case BOTTOM -> { startY = 1; endY = 0; startX = 0; endX = 0; }
            case LEFT -> { startX = 0; endX = 1; startY = 0; endY = 0; }
            case RIGHT -> { startX = 1; endX = 0; startY = 0; endY = 0; }
        }

        Color baseColor = Color.web("#6b7785");
        Color fadeColor = Color.web("#2a2e33", 0.15);

        LinearGradient gradient = new LinearGradient(
                startX, startY, endX, endY, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, baseColor),
                new Stop(1.0, fadeColor)
        );

        tempGc.setFill(gradient);
        tempGc.fillRect(0, 0, width, height);

        tempGc.setStroke(Color.web("#2a2e33"));
        tempGc.setLineWidth(4);

        switch (blockDirection) {
            case TOP -> tempGc.strokeLine(0, 2, width, 2);
            case BOTTOM -> tempGc.strokeLine(0, height - 2, width, height - 2);
            case LEFT -> tempGc.strokeLine(2, 0, 2, height);
            case RIGHT -> tempGc.strokeLine(width - 2, 0, width - 2, height);
        }

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
    }
}