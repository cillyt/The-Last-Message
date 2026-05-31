/*
  Протагоніст
 */

package backend;

public class Player extends MovingGameEntity{

    public Player(int x, int y) {
        super(x, y);

        height = 170;
        // при присіданні height = 100
        width = 50;

        this.targetJumpHeight = 120;
        this.startJumpSpeed = -Math.sqrt(2 * GRAVITY * this.targetJumpHeight);
    }
}
