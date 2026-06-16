package backend.weapon;

import backend.SoundManager;

public class Pistol extends Weapon{

    public Pistol () {
        cooldown = 0.7;
        currentFireTime = 1;

        ammunitionNumber = 999;
        bullets = new PistolBullet [5];
        for (int i = 0; i < bullets.length; i++){
            bullets[i] = new PistolBullet();
        }

        noiseLevel = 1;
        shotSound = SoundManager.SoundType.pistolShot;
    }
}
