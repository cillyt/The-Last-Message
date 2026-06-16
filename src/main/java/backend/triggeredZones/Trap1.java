/*
  Зелений слиз, що наносить шкоду гравцеві
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

public class Trap1 extends Detector {

    public Trap1(int x, int y) {
        super(x, y);
        zIndex = 6;

        width = 100;
        height = 10;

        timePeriod = 1;

        triggerOnce = false;
        havePeriodicActoin = true;

        currentImage = new Image("file:assets/detectors/назва.png");

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#8B0000"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.BLACK);
        tempGc.setFont(new Font("Arial", 10));
        tempGc.fillText("TRAP1", 2, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
    }

    @Override
    protected void doPeriodicAction(){
        if(checkCollision(targetPlayer)) targetPlayer.takeDamage(10);
    }

    @Override
    protected void onEnter(GameEntity entity) {
        if (entity instanceof Player player) {
            player.setSpeedModifier(0.2);
        }
    }

    @Override
    protected void onExit(GameEntity entity) {
        if (entity instanceof Player player) {
            player.setSpeedModifier(1.0);
        }
    }
}
