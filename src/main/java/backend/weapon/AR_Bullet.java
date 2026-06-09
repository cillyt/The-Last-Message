package backend.weapon;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class AR_Bullet extends Bullet{
    public AR_Bullet() {
        super();
        damage = 10;
        speedX = 1500;

        baseWidth = 15;
        width = 15;

        baseHeight = 3;
        height = 3;

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#FFFF99"));
        tempGc.fillRect(0, 0, width, height);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
    }
}
