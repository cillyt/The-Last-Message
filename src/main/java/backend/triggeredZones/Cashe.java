/*
  Схованка в стіні з приписами чи іншими приколюхами
  Маскується під стіну
  Коли гравець на неї заходить - стає майже прозорою
 */

package backend.triggeredZones;

import backend.GameEntity;
import backend.Player;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Cashe extends Detector {

    private Image solidImage;
    private Image transparentImage;

    public Cashe(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.zIndex = 6;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        // щільна текстура
        drawTexture(tempGc, width, height);
        solidImage = canvas.snapshot(params, null);

        // прозора текстура
        tempGc.clearRect(0, 0, width, height);
        tempGc.setGlobalAlpha(0.3);
        drawTexture(tempGc, width, height);
        transparentImage = canvas.snapshot(params, null);

        this.image = solidImage;
    }

    // Допоміжний метод, щоб не дублювати код малювання ліній
    private void drawTexture(GraphicsContext tempGc, int w, int h) {
        tempGc.setFill(Color.web("#65707d"));
        tempGc.fillRect(0, 0, w, h);

        tempGc.setStroke(Color.web("#2a2e33"));
        tempGc.setLineWidth(4);
        tempGc.strokeRect(2, 2, w - 4, h - 4);

        tempGc.setStroke(Color.web("#505a66"));
        tempGc.setLineWidth(2);
        tempGc.strokeLine(8, 10, w - 8, 10);
        tempGc.strokeLine(8, 16, w - 8, 16);
    }

    @Override
    protected void onEnter(GameEntity entity) {
        if (entity instanceof Player) {
            this.image = transparentImage;
        }
    }

    @Override
    protected void onExit(GameEntity entity) {
        if (entity instanceof Player) {
            this.image = solidImage;
        }
    }
}