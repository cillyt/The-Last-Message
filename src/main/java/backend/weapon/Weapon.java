package backend.weapon;

import backend.Level;
import backend.Player;
import backend.SoundManager;
import backend.SoundPrint;
import lombok.Getter;

@Getter
public abstract class Weapon {

    protected double cooldown;
    protected double currentFireTime;
    protected int ammunitionNumber;

    protected boolean isShooting = false;

    protected Bullet[] bullets;

    public double noiseLevel;

    protected SoundManager.SoundType shotSound;

    public void startFire() {
        isShooting = true;
    }

    public void stopFire() {
        isShooting = false;
    }

    protected void fire() {
        if (currentFireTime >= cooldown && ammunitionNumber > 0) {
            double angle = Player.getInstance().isFacingRight() ? 0 : Math.PI;

            for (int i = 0; i < bullets.length; i++) {
                if (!bullets[i].isFlying) {
                    bullets[i].shootOut(angle);
                    currentFireTime = 0;
                    if (!(this instanceof Pistol)) ammunitionNumber--;

                    Level.getCurrentLevel().getSoundPrints().add(
                            new SoundPrint(Player.getInstance().getX(), Player.getInstance().getY(), noiseLevel));
                    SoundManager.getInstance().playSound(shotSound);
                    return;
                }
            }
        }
        if(ammunitionNumber <= 0) SoundManager.getInstance().playSound(SoundManager.SoundType.noBullet);
    }

    public void addAmmunition(int amount) {
        ammunitionNumber += amount;
    }

    public void update(double deltaTime) {
        if (currentFireTime < cooldown) currentFireTime += deltaTime;


        if (isShooting) fire();

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}