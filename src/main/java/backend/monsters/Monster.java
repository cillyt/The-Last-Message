package backend.monsters;

import backend.MovingGameEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Monster extends MovingGameEntity {

    protected int hp;

    public Monster(int x, int y) {
        super(x, y);
    }


    public void takeDamage(int damage) {
        hp -= damage;
    }
}
