package backend.weapon;

public class Pistol extends Weapon{

    public Pistol () {
        cooldown = 1;
        currentFireTime = 1;

        ammunitionNumber = 999;
        bullets = new PistolBullet [5];
        for (int i = 0; i < bullets.length; i++){
            bullets[i] = new PistolBullet();
        }
    }
}
