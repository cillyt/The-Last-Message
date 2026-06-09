package backend;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GameEntity {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int zIndex;
    protected boolean inCamera;
    protected boolean isActive = true;
    protected Image image; // заглушка
    protected Image currentImage; // справжній спрайт

    protected boolean isWalkable;

    public GameEntity() {
        x = -1000;
        y = -1000;
    }

    public GameEntity (int x, int y){
        this.x = x;
        this.y = y;
    }

    public GameEntity (int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GameEntity (int x, int y, int width, int height, boolean isWalkable){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isWalkable = isWalkable;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }

    public double calcDistance(int targetX, int targetY) {
        int a = x - targetX;
        int b = y - targetY;
        return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    public void render(GraphicsContext gc) {
        if(!isActive) return;

        CameraWindow camera = CameraWindow.getInstance();

        boolean isVisible = (x + width > camera.getX()) &&
                (x < camera.getX() + camera.getScreenWidth()) &&
                (y + height > camera.getY()) &&
                (y < camera.getY() + camera.getScreenHeight());

        inCamera = isVisible;

        if (isVisible && image != null) {
            int screenX = this.x - camera.getX();
            int screenY = this.y - camera.getY();

            gc.drawImage(image, screenX, screenY, width, height);

            if (currentImage != null) {
                if (this instanceof MovingGameEntity && !((MovingGameEntity) this).isFacingRight()) { // перевіряємо чи обернений об'єкт ліворуч
                    gc.save();

                    gc.translate(screenX + width, screenY);
                    gc.scale(-1, 1);

                    gc.drawImage(currentImage, 0, 0, width, height);

                    gc.restore();
                } else gc.drawImage(currentImage, screenX, screenY, width, height);

            }
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}