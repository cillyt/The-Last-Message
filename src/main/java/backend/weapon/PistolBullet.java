package backend.weapon;

public class PistolBullet extends Bullet{
    public PistolBullet() {
        super();
        this.damage = 3;
        speedX = 800;

        baseWidth = 7;
        baseHeight = 5;
    }
}
