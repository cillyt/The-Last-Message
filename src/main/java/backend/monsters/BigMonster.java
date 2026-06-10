/*
  Великий монстр, багато здоров'я і шкоди
 */

package backend.monsters;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BigMonster extends Monster {

    public BigMonster(int x, int y) {
        super(x, y);

        height = 200;
        width = 150;

        speedX = 350;

        maxHp = 250;
        currentHP = 250;

        targetJumpHeight = 120;
        initialJumpParams();

        toPatrol();

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#DC143C"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.WHITE);
        tempGc.setFont(new Font("Arial", 12));
        tempGc.fillText("BIG_MONST", 5, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
    }
}
