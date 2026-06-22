/*
  Великий монстр, багато здоров'я і шкоди
 */

package backend.monsters;

import backend.SoundManager;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.nio.file.Paths;

public class BigMonster extends Monster {

    public BigMonster(int x, int y, int patrolRadius) {
        super(x, y, patrolRadius);

        width = 70;
        height = 180;

        topImgMarg = 7;
        sideImgMarg = 40;

        speedX = 150;

        maxHp = 100;
        currentHP = maxHp;

        damage = 25;
        cooldown = 1;

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
        standImg = new Image(getAssetPath("assets/monsters/big_monster/move/WALK_08.png"));

        jumpImg = new Image(getAssetPath("assets/monsters/big_monster/move/WALK_04.png"));

        moveImgs = new Image[]{

                new Image(getAssetPath("assets/monsters/big_monster/move/WALK_02.png")),
                new Image(getAssetPath("assets/monsters/big_monster/move/WALK_03.png")),
                new Image(getAssetPath("assets/monsters/big_monster/move/WALK_04.png")),
                new Image(getAssetPath("assets/monsters/big_monster/move/WALK_05.png")),
                new Image(getAssetPath("assets/monsters/big_monster/move/WALK_06.png")),
                new Image(getAssetPath("assets/monsters/big_monster/move/WALK_07.png")),
                new Image(getAssetPath("assets/monsters/big_monster/move/WALK_08.png")),

        };

        attackImgs = new Image[]{
               new Image(getAssetPath("assets/monsters/big_monster/attack/ATTACK_02.png")),
               new Image(getAssetPath("assets/monsters/big_monster/attack/ATTACK_03.png")),
               new Image(getAssetPath("assets/monsters/big_monster/attack/ATTACK_04.png")),
               new Image(getAssetPath("assets/monsters/big_monster/attack/ATTACK_05.png")),
        };

        dyingImgs = new Image[]{
                new Image(getAssetPath("assets/monsters/big_monster/dying/DEAD_01.png")),
                new Image(getAssetPath("assets/monsters/big_monster/dying/DEAD_02.png")),
                new Image(getAssetPath("assets/monsters/big_monster/dying/DEAD_03.png")),
        };
        // -----------------

        calcImgMarg(1.0, moveImgs[0]);

        agroSound = SoundManager.SoundType.bigAgro;
        deathSound = SoundManager.SoundType.bigDeath;
        attackSound = SoundManager.SoundType.bigAttack;

        initialTimePeriods();
    }
}
