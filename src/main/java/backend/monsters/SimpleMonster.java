/*
  Звичаний, стартовий монстр
 */

package backend.monsters;

import backend.MovingGameEntity;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SimpleMonster extends MovingGameEntity {

    public SimpleMonster(int x, int y) {
        super(x, y);

        height = 150;
        width = 50;

        this.targetJumpHeight = 120;
        this.startJumpSpeed = -Math.sqrt(2 * gravity * this.targetJumpHeight);

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#FF8C00"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.BLACK);
        tempGc.setFont(new Font("Arial", 10));
        tempGc.fillText("SIMP_Monst", 2, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
    }
}
