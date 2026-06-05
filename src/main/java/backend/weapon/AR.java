package backend.weapon;

public class AR extends Weapon{

    public AR () {
        cooldown = 0.1;
        currentFireTime = 0.1;

        ammunitionNumber = 15;
        isShooting = false;


        bullets = new AR_Bullet [15];
        for (int i = 0; i < bullets.length; i++){
            bullets[i] = new AR_Bullet();
        }
    }
}
