/*
  Дрібний монстр, який пересувається стрибками і персувається групами
 */

package backend.monsters;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LeapingMonster extends Monster {

    public LeapingMonster(int x, int y, int patrolRadius) {
        super(x, y, patrolRadius);

        width = 50;
        height = 40;

        speedX = 270;

        maxHp = 5;
        currentHP = maxHp;

        targetJumpHeight = 75;
        initialJumpParams();

        damage = 3;
        cooldown = 0.5;

        toPatrol();

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#DC143C"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.WHITE);
        tempGc.setFont(new Font("Arial", 12));
        tempGc.fillText("LEAP_MONST", 5, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------

        // ----- АСЕТИ -----
        standImg = new Image("file:assets/monsters/leap_monster/назва.png");
        jumpImg = new Image("file:assets/monsters/leap_monster/назва.png");

        moveImgs = new Image[]{
                new Image("file:assets/monsters/leap_monster/move/назва.png"),
                new Image("file:assets/monsters/leap_monster/move/назва.png"),
                new Image("file:assets/monsters/leap_monster/move/назва.png"),
                new Image("file:assets/monsters/leap_monster/move/назва.png"),
                // ...
        };
        attackImgs = new Image[]{
                new Image("file:assets/monsters/leap_monster/attack/назва.png"),
                new Image("file:assets/monsters/leap_monster/attack/назва.png"),
                new Image("file:assets/monsters/leap_monster/attack/назва.png"),
                new Image("file:assets/monsters/leap_monster/attack/назва.png"),
                // ...
        };
        dyingImgs = new Image[]{
                new Image("file:assets/monsters/leap_monster/dying/назва.png"),
                new Image("file:assets/monsters/leap_monster/dying/назва.png"),
                new Image("file:assets/monsters/leap_monster/dying/назва.png"),
                new Image("file:assets/monsters/leap_monster/dying/назва.png"),
                // ...
        };
        // -----------------
        initialTimePeriods();
    }

    @Override
    protected void checkPathAndMove() {
        if (!onGround) return;

        PathCondition obstacle = checkObstacle();
        PathCondition hole = checkHole();

        if (obstacle == PathCondition.IMPASSABLE || hole == PathCondition.IMPASSABLE) {
            handleImpassable();
        } else {
            currentVelocityX = facingRight ? speedX : -speedX;
            currentVelocityY = startJumpSpeed;
            currentState = State.IN_AIR;
            onGround = false;

            currentImage = jumpImg;
        }
    }
}