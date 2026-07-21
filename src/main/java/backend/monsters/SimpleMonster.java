/*
  Звичаний, стартовий монстр
 */

package backend.monsters;

import backend.SoundManager;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SimpleMonster extends Monster {

    public SimpleMonster(int x, int y, int patrolRadius) {
        super(x, y, patrolRadius);

        width = 120;
        height = 125;

        speedX = 200;

        maxHp = 10;
        currentHP = maxHp;

        this.targetJumpHeight = 120;
        initialJumpParams();

        damage = 10;
        cooldown = 0.7;

        toPatrol();
        
        // ----- АСЕТИ -----
        standImg = new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/scifi_alien_idle_2.png"));
        jumpImg = new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/move/scifi_alien_run_2.png"));


                moveImgs = new Image[]{

                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/move/scifi_alien_run_1.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/move/scifi_alien_run_2.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/move/scifi_alien_run_3.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/move/scifi_alien_run_4.png")),

        };
        attackImgs = new Image[]{

                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/attack/scifi_alien_bite_1.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/attack/scifi_alien_bite_2.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/attack/scifi_alien_bite_3.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/attack/scifi_alien_bite_4.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/attack/scifi_alien_bite_5.png")),

        };
        dyingImgs = new Image[]{

                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/dying/scifi_alien_die_1.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/dying/scifi_alien_die_2.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/dying/scifi_alien_die_3.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/dying/scifi_alien_die_4.png")),
                new Image(getClass().getResourceAsStream("/assets/monsters/simp_monster/dying/scifi_alien_die_5.png")),

        };
        // -----------------

        calcImgMarg(2, standImg);

        agroSound = SoundManager.SoundType.simpAgro;
        deathSound = SoundManager.SoundType.simpDeath;
        attackSound = SoundManager.SoundType.simpAttack;

        initialTimePeriods();
    }
}
