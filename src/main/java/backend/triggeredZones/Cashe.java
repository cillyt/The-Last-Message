/*
  Схованка в стіні з приписами чи іншими приколюхами
  Маскується під стіну
  Коли гравець на неї заходить - стає майже прозорою
 */

package backend.triggeredZones;

import backend.CameraWindow;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Cashe extends Detector {

    private Image cachedTexture;

    public Cashe(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.zIndex = 2;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();

        tempGc.setFill(Color.web("#65707d"));
        tempGc.fillRect(0, 0, width, height);

        tempGc.setStroke(Color.web("#2a2e33"));
        tempGc.setLineWidth(4);
        tempGc.strokeRect(2, 2, width - 4, height - 4);

        tempGc.setStroke(Color.web("#505a66"));
        tempGc.setLineWidth(2);
        tempGc.strokeLine(8, 10, width - 8, 10);
        tempGc.strokeLine(8, 16, width - 8, 16);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        cachedTexture = canvas.snapshot(params, null);
    }
}