/*
  Великий монстр, багато здоров'я і шкоди
 */

package backend.monsters;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BigMonster extends Monster {

    public BigMonster(int x, int y, int patrolRadius) {
        super(x, y, patrolRadius);

        height = 200;
        width = 150;

        speedX = 150;

        maxHp = 150;
        currentHP = 150;

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

        // ----- АСЕТИ -----
        standImg = new Image("file:assets/monsters/big_monster/назва.png");
        jumpImg = new Image("file:assets/monsters/big_monster/назва.png");

        moveImgs = new Image[]{
                new Image("file:assets/monsters/big_monster/move/назва.png"),
                new Image("file:assets/monsters/big_monster/move/назва.png"),
                new Image("file:assets/monsters/big_monster/move/назва.png"),
                new Image("file:assets/monsters/big_monster/move/назва.png"),
                // ...
        };
        attackImgs = new Image[]{
                new Image("file:assets/monsters/big_monster/attack/назва.png"),
                new Image("file:assets/monsters/big_monster/attack/назва.png"),
                new Image("file:assets/monsters/big_monster/attack/назва.png"),
                new Image("file:assets/monsters/big_monster/attack/назва.png"),
                // ...
        };
        dyingImgs = new Image[]{
                new Image("file:assets/monsters/big_monster/dying/назва.png"),
                new Image("file:assets/monsters/big_monster/dying/назва.png"),
                new Image("file:assets/monsters/big_monster/dying/назва.png"),
                new Image("file:assets/monsters/big_monster/dying/назва.png"),
                // ...
        };
        // -----------------

        initialTimePeriods();
    }
}
