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
    protected int topImgMarg;
    protected int sideImgMarg; // відступи зверху і з боків для зображень

    protected boolean isWalkable;

    public GameEntity() {
        x = -1000;
        y = -1000;
    }

    public GameEntity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GameEntity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GameEntity(int x, int y, int width, int height, boolean isWalkable) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isWalkable = isWalkable;
    }

    /*
      Викликати в нащадках, кому потрібно
     */
    protected void calcImgMarg(double imgSizeCoef, Image image){
        int finalImgWidth = (int) (image.getWidth() * imgSizeCoef);
        int finalImgHeight = (int) (image.getHeight() * imgSizeCoef);

        topImgMarg = finalImgHeight - height;
        sideImgMarg = (finalImgWidth - width) / 2;
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

        if (isVisible) {
            int screenX = this.x - camera.getX();
            int screenY = this.y - camera.getY();

            if (image != null) {
                gc.drawImage(image, screenX, screenY, width, height);
            }

            int drawX = screenX - sideImgMarg;
            int drawY = screenY - topImgMarg;
            int drawW = width + 2 * sideImgMarg;
            int drawH = height + topImgMarg;

            if (this instanceof Player) drawY += 2;

            if (currentImage != null) {
                if (this instanceof MovingGameEntity && !((MovingGameEntity) this).isFacingRight()) {
                    gc.save();

                    gc.translate(screenX + width / 2.0, screenY);
                    gc.scale(-1, 1);

                    gc.drawImage(currentImage, -width / 2.0 - sideImgMarg, -topImgMarg, drawW, drawH);

                    gc.restore();
                } else {
                    gc.drawImage(currentImage, drawX, drawY, drawW, drawH);
                }
            }
        }
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }


    public static String getAssetPath(String relativePath) {
        return java.nio.file.Paths.get(relativePath).toUri().toString();
    }
}