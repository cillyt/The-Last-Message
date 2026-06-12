/*
Автомат, який лежить на землі
Після його підбирання герой матиме можливість їм користуватися
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

public class AR_Item extends Detector {

    public AR_Item(int x, int y) {
        super(x, y);

        width = 50;
        height = 50;

        zIndex = 3;

        currentImage = new Image("file:assets/detectors/назва.png");

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#B026FF"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.WHITE);
        tempGc.setFont(new Font("Arial", 10));
        tempGc.fillText("AR", 5, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
    }

    @Override
    protected void onExit(GameEntity entity) {
        isTriggered = true;

        Player.getInstance().unlockWeapon(1);
        isActive = false;
    }
}
