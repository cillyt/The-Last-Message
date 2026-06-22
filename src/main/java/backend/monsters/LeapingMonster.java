/*
  Дрібний монстр, який пересувається стрибками і персувається групами
 */

package backend.monsters;

import backend.Player;
import backend.SoundManager;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class LeapingMonster extends Monster {

    public LeapingMonster(int x, int y, int patrolRadius) {
        super(x, y, patrolRadius);

        width = 65;
        height = 54;

        speedX = 270;

        maxHp = 5;
        currentHP = maxHp;

        targetJumpHeight = 90;
        initialJumpParams();

        damage = 2;
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
        //this.image = canvas.snapshot(params, null);
        // ----------------

        // ----- АСЕТИ -----
        standImg = new Image(getAssetPath("assets/monsters/leap_monster/IDLE_01.png"));
        jumpImg = new Image(getAssetPath("assets/monsters/leap_monster/move/WALK_03.png"));


                moveImgs = new Image[]{
                new Image(getAssetPath("assets/monsters/leap_monster/move/WALK_01.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/move/WALK_02.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/move/WALK_03.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/move/WALK_04.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/move/WALK_05.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/move/WALK_06.png")),
        };
        attackImgs = new Image[]{
                new Image(getAssetPath("assets/monsters/leap_monster/attack/ATTACK_01.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/attack/ATTACK_02png")),
                new Image(getAssetPath("assets/monsters/leap_monster/attack/ATTACK_03.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/attack/ATTACK_04.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/attack/ATTACK_05.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/attack/ATTACK_06.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/attack/ATTACK_07.png")),
        };
        dyingImgs = new Image[]{
                new Image(getAssetPath("assets/monsters/leap_monster/dying/DEAD_01.png")),
                new Image(getAssetPath("assets/monsters/leap_monster/dying/DEAD_02.png")),
        };
        // -----------------

        calcImgMarg(1.5, jumpImg);

        agroSound = SoundManager.SoundType.leapAgro;
        deathSound = SoundManager.SoundType.leapDeath;
        attackSound = SoundManager.SoundType.leapAttack;

        initialTimePeriods();
    }

    private boolean isPlayerAbove() {
        Player p = Player.getInstance();

        // чи перетинаються координати по горизонталі
        boolean overlapX = (this.x < p.getX() + p.getWidth()) && (this.x + this.width > p.getX());

        // чи ноги гравця знаходяться над головою монстра
        boolean overlapY = (p.getY() + p.getHeight() >= this.y - 150) && (p.getY() + p.getHeight() <= this.y + 30);

        return overlapX && overlapY;
    }

    @Override
    protected void checkPathAndMove() {
        if (!onGround) return;

        // якщо гравець прямо над головою - не стрибаємо
        if (isPlayerAbove()) {
            currentVelocityX = 0;
            currentState = State.STAND;
            currentImage = standImg;
            return;
        }

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