/*
  Великий монстр, багато здоров'я і шкоди
 */

package backend.monsters;

import backend.MovingGameEntity;

public class BigMonster extends MovingGameEntity {

    public BigMonster(int x, int y) {
        super(x, y);

        height = 200;
        width = 150;

        this.targetJumpHeight = 120;
        this.startJumpSpeed = -Math.sqrt(2 * gravity * this.targetJumpHeight);
    }
}
