package backend.weapon;

import backend.*;
import backend.background.Sticker;
import backend.monsters.Monster;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Getter;

@Getter
public abstract class Bullet extends MovingGameEntity {
    protected int damage;
    protected boolean isFlying;

    protected int baseWidth;
    protected int baseHeight;

    protected int barrelX;
    protected int barrelY;
    protected boolean justFired = false;

    protected Image fireImg = new Image(getAssetPath("assets/player/fire.png"));
    protected Sticker fireFlash;
    protected double currentFlashTime = 0;
    protected double flashTime = 0.08;

    public Bullet() {
        super();
        this.isFlying = false;
        zIndex = 7;

        fireFlash = new Sticker(-1000, -1000, fireImg);
        fireFlash.setActive(false);
    }

    public void shootOut(double angle) {
        Player player = Player.getInstance();

        updateBarrelPosition(player);

        this.x = barrelX;
        this.y = barrelY;

        this.width = (int) (Math.abs(baseWidth * Math.cos(angle)) + Math.abs(baseHeight * Math.sin(angle)));
        this.height = (int) (Math.abs(baseWidth * Math.sin(angle)) + Math.abs(baseHeight * Math.cos(angle)));

        this.currentVelocityX = speedX * Math.cos(angle);
        this.currentVelocityY = speedX * Math.sin(angle);

        this.isFlying = true;
        this.justFired = true;

        updateFlashPosition(player);
        fireFlash.setActive(true);
        currentFlashTime = 0;
    }

    protected void updateFlashPosition(Player player) {
        // Оновлюємо змінні barrelX та barrelY
        updateBarrelPosition(player);

        int flashX = player.isFacingRight() ? barrelX : barrelX - fireFlash.getWidth() + this.width;

        fireFlash.setX(flashX);
        fireFlash.setY(barrelY - fireFlash.getHeight() / 2);
        fireFlash.setFacingRight(player.isFacingRight());
    }

    protected void updateBarrelPosition(Player player) {

        if(player.getCurrentWeapon() instanceof Pistol){
            barrelX = player.isFacingRight() ? player.getX() + player.getWidth() + 23 : player.getX() - this.width - 21;
            barrelY = !player.isCrouching() ? player.getY() + 38 : player.getY() + 30;

            if (player.isCrouching())
                barrelX += player.isFacingRight() ? 12 : -12;

            else if (player.getCurrentState() == State.GO) {
                barrelX += player.isFacingRight() ? 8 : -8;
                barrelY += 3;
            } else if(player.getCurrentState() == State.IN_AIR){
                barrelX += player.isFacingRight() ? 6 : -6;
            }
        }

        if (player.getCurrentWeapon() instanceof AR){
            barrelX = player.isFacingRight() ? player.getX() + player.getWidth() + 48 : player.getX() - this.width - 46;
            barrelY = !player.isCrouching() ? player.getY() + 34 : player.getY() + 57;

            if (player.isCrouching()) {
                barrelX += player.isFacingRight() ? -3 : 3;

                if (player.getCurrentState() == State.GO){
                    barrelX += player.isFacingRight() ? 1 : -1;
                    barrelY += -13;
                }
            } else if (player.getCurrentState() == State.GO) {
                barrelX += player.isFacingRight() ? -10 : 10;
                barrelY += 27;
            } else if (player.getCurrentState() == State.IN_AIR){
                barrelY += 7;
            }
        }

    }

    public void deactivate() {
        this.isFlying = false;
        this.currentVelocityX = 0;
        this.currentVelocityY = 0;
        this.x = -1000;
        this.y = -1000;
    }

    @Override
    public void update(double deltaTime) {
        if(fireFlash.isActive()){
            currentFlashTime += deltaTime;
            if(currentFlashTime >= flashTime){
                currentFlashTime = 0;
                fireFlash.setActive(false);
            } else {
                updateFlashPosition(Player.getInstance());
            }
        }

        if (!isFlying) return;

        // скіпаємо рух у першому кадрі
        if (justFired) {
            justFired = false;
            return;
        }

        subPixelX += currentVelocityX * deltaTime;
        subPixelY += currentVelocityY * deltaTime;
        int deltaX = (int) subPixelX;
        int deltaY = (int) subPixelY;

        if (deltaX == 0 && deltaY == 0) return;

        subPixelX -= deltaX;
        subPixelY -= deltaY;

        int sweepX = Math.min(this.x, this.x + deltaX);
        int sweepY = Math.min(this.y, this.y + deltaY);
        int sweepW = this.width + Math.abs(deltaX);
        int sweepH = this.height + Math.abs(deltaY);

        GameEntity hitObject = collision(sweepX, sweepY, sweepW, sweepH, deltaX, deltaY, Level.getCurrentLevel().getBlokingObjects());


        if (hitObject != null) {
            if (hitObject instanceof Monster) ((Monster) hitObject).takeDamage(damage);
            deactivate();
        } else {
            this.x += deltaX;
            this.y += deltaY;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (fireFlash.isActive()) {
            fireFlash.render(gc);
        }

        if (!isFlying) return;
        super.render(gc);
    }


}