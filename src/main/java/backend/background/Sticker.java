/*
  Декоративна наліпка, яка ставиться поверх фону і землі
  Це може бути кров, потряпина і тп
 */

package backend.background;

import backend.CameraWindow;
import backend.GameEntity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Sticker extends GameEntity {

    private boolean facingRight = true;

    public Sticker(int x, int y){
        super(x, y);
        isWalkable = true;
        zIndex = 7;
    }

    public Sticker(int x, int y, Image image) {
        super(x, y);
        this.image = image;
        width = (int) image.getWidth();
        height = (int) image.getHeight();
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

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
                if (!facingRight) {
                    gc.save();
                    gc.translate(screenX + width / 2.0, screenY);
                    gc.scale(-1, 1);
                    gc.drawImage(image, -width / 2.0, 0, width, height);
                    gc.restore();
                } else {
                    gc.drawImage(image, screenX, screenY, width, height);
                }
            }
        }
    }
}

