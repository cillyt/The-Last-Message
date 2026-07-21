/*
  Поповнення набоїв для автомата
 */

package backend.triggeredZones;

import backend.GameEntity;
import backend.Player;
import backend.weapon.AR;
import backend.weapon.Weapon;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class AR_Ammunition extends Detector {
    private int number; // кількість набоїв

    public AR_Ammunition(int x, int y, int number) {
        super(x, y);
        this.number = number;

        width = 40;
        height = 40;

        zIndex = 3;

        currentImage = new Image(getClass().getResourceAsStream("/assets/detectors/bullets.png"));

        calcImgMarg(0.3, currentImage);
    }

    @Override
    protected void onEnter(GameEntity entity) {
        isTriggered = true;

        Weapon[] weapons = Player.getInstance().getWeapons();
        for (Weapon weapon : weapons){
            if(weapon instanceof AR) {
                weapon.addAmmunition(number);
                isActive = false;
                return;
            }
        }
    }
}
