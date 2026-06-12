/*
  Звичаний, стартовий монстр
 */

package backend.monsters;

import backend.MovingGameEntity;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SimpleMonster extends Monster {

    public SimpleMonster(int x, int y, int patrolRadius) {
        super(x, y, patrolRadius);

        height = 150;
        width = 50;

        speedX = 200;

        maxHp = 10;
        currentHP = maxHp;


        this.targetJumpHeight = 120;
        initialJumpParams();

        toPatrol();

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
        
        // ----- АСЕТИ -----
        standImg = new Image("file:assets/monsters/simp_monster/назва.png");
        jumpImg = new Image("file:assets/monsters/simp_monster/назва.png");

        moveImgs = new Image[]{
                new Image("file:assets/monsters/simp_monster/move/назва.png"),
                new Image("file:assets/monsters/simp_monster/move/назва.png"),
                new Image("file:assets/monsters/simp_monster/move/назва.png"),
                new Image("file:assets/monsters/simp_monster/move/назва.png"),
                // ...
        };
        attackImgs = new Image[]{
                new Image("file:assets/monsters/simp_monster/attack/назва.png"),
                new Image("file:assets/monsters/simp_monster/attack/назва.png"),
                new Image("file:assets/monsters/simp_monster/attack/назва.png"),
                new Image("file:assets/monsters/simp_monster/attack/назва.png"),
                // ...
        };
        dyingImgs = new Image[]{
                new Image("file:assets/monsters/simp_monster/dying/назва.png"),
                new Image("file:assets/monsters/simp_monster/dying/назва.png"),
                new Image("file:assets/monsters/simp_monster/dying/назва.png"),
                new Image("file:assets/monsters/simp_monster/dying/назва.png"),
                // ...
        };
        // -----------------

        initialTimePeriods();
    }
}
