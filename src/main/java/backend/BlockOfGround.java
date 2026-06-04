package backend;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BlockOfGround extends GameEntity {

    public BlockOfGround(int x, int y, int width, int height) {
        super(x, y, width, height, false);
        isWalkable = false;
        this.zIndex = 1;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();

        tempGc.setFill(Color.web("#6b7785"));
        tempGc.fillRect(0, 0, width, height);

        tempGc.setStroke(Color.web("#2a2e33"));
        tempGc.setLineWidth(4);
        tempGc.strokeRect(2, 2, width - 4, height - 4);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        image = canvas.snapshot(params, null);
    }
}