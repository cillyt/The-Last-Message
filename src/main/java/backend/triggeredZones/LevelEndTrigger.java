/*
  Зона закінчення рівня
 */

package backend.triggeredZones;

import backend.GameEntity;

import backend.Level;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LevelEndTrigger extends Detector {
    public LevelEndTrigger(int x, int y, int width, int height) {
        super(x, y, width, height);

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#FF00FF"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.BLACK);
        tempGc.setFont(new Font("Arial", 12));
        tempGc.fillText("EXIT", 5, 20);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
    }

    @Override
    protected void onEnter(GameEntity entity) {
        Level.getCurrentLevel().win();
    }
}
