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

        // Викликається вікно з ui
    }

    @Override
    protected void onEnter(GameEntity entity) {
        Level.getCurrentLevel().win();
    }
}
