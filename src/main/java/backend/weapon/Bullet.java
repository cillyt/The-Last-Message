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

        // --- ЗАГЛУШКА ---

        // ----------------
    }

    public void shootOut(double angle) {
        Player player = Player.getInstance();
        if (player.isFacingRight()) {
            x = player.getX() + player.getWidth() + 1;
        }
        else x = player.getX() - width - 1;
        y = player.getY() + 80;
        if(player.isCrouching()) y -= 40;

        this.width = (int) (Math.abs(baseWidth * Math.cos(angle)) + Math.abs(baseHeight * Math.sin(angle)));
        this.height = (int) (Math.abs(baseWidth * Math.sin(angle)) + Math.abs(baseHeight * Math.cos(angle)));

        this.currentVelocityX = speedX * Math.cos(angle);
        this.currentVelocityY = speedX * Math.sin(angle);

        this.isFlying = true;

        // спалах
        fireFlash.setX(player.isFacingRight() ? x : x - fireFlash.getWidth());
        fireFlash.setY(y - fireFlash.getHeight() / 2);
        fireFlash.setFacingRight(player.isFacingRight());
        fireFlash.setActive(true);
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
            }
        }

        if (!isFlying) return;

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