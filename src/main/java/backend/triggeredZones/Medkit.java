/*
  Аптечка
 */

package backend.triggeredZones;

import backend.GameEntity;
import backend.Player;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Medkit extends Detector {

    public Medkit(int x, int y) {
        super(x, y);

        width = 40;
        height = 40;

        zIndex = 3;

        currentImage = new Image("file:assets/detectors/medkit.png");

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
       // GraphicsContext tempGc = canvas.getGraphicsContext2D();
       // tempGc.setFill(Color.web("#32CD32"));
       // tempGc.fillRect(0, 0, width, height);
      //  tempGc.setFill(Color.BLACK);
      //  tempGc.setFont(new Font("Arial", 10));
      //  tempGc.fillText("HP", 5, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
    }

    @Override
    protected void onEnter(GameEntity entity) {
        isTriggered = true;

        Player.getInstance().healHp(20);

        isActive = false;
    }
}
