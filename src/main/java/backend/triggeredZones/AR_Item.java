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

        currentImage = new Image(getClass().getResourceAsStream("/assets/detectors/ar.png"));

        calcImgMarg(1, currentImage);
    }

    @Override
    protected void onEnter(GameEntity entity) {
        isTriggered = true;

        Player.getInstance().unlockWeapon(1);
        isActive = false;
    }
}
