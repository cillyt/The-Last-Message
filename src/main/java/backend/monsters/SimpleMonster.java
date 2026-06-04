/*
  Звичаний, стартовий монстр
 */

package backend.monsters;

import backend.MovingGameEntity;

public class SimpleMonster extends MovingGameEntity {

    public SimpleMonster(int x, int y) {
        super(x, y);

        height = 150;
        width = 50;

        this.targetJumpHeight = 120;
        this.startJumpSpeed = -Math.sqrt(2 * gravity * this.targetJumpHeight);
    }
}
