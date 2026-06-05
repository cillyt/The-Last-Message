package backend.weapon;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PistolBullet extends Bullet{
    public PistolBullet() {
        super();
        this.damage = 3;
        speedX = 800;

        baseWidth = 7;
        width = 7;

        baseHeight = 5;
        height = 5;

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
