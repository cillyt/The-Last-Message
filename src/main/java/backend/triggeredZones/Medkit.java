/*
  Аптечка
 */

package backend.triggeredZones;

import backend.GameEntity;
import backend.Player;
import backend.SoundManager;
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

        currentImage = new Image(getClass().getResourceAsStream("/assets/detectors/medkit.png"));

        calcImgMarg(0.4, currentImage);
    }

    @Override
    protected void onEnter(GameEntity entity) {
        isTriggered = true;

        Player.getInstance().healHp(20);

        SoundManager.getInstance().play(SoundManager.SoundType.healing1);

        isActive = false;
    }
}
