package backend.weapon;

import backend.MovingGameEntity;
import backend.Player;
import backend.Level;
import backend.GameEntity;
import backend.monsters.Monster;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class Bullet extends MovingGameEntity {
    protected int damage;
    protected boolean isFlying;

    protected int baseWidth;
    protected int baseHeight;

    public Bullet() {
        super(0, 0);
        this.isFlying = false;
        zIndex = 7;

        // --- ЗАГЛУШКА ---

        // ----------------
    }

    public void shootOut(double angle) {
        Player player = Player.getInstance();
        this.x = player.getX() + (player.getWidth() / 2);
        this.y = player.getY() + (player.getHeight() / 2);

        this.isFlying = true;

        this.currentVelocityX = speedX * Math.cos(angle);
        this.currentVelocityY = speedX * Math.sin(angle);

        this.width = (int) (Math.abs(baseWidth * Math.cos(angle)) + Math.abs(baseHeight * Math.sin(angle)));
        this.height = (int) (Math.abs(baseWidth * Math.sin(angle)) + Math.abs(baseHeight * Math.cos(angle)));
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

        GameEntity hitObject = collision(sweepX, sweepY, sweepW, sweepH, Level.getCurrentLevel().getBlokingObjects());


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
        if (!isFlying) return;
        super.render(gc);
    }
}