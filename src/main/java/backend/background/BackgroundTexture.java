/*
  Фонова текстура, що складається лише з зображення
 */

package backend.background;

import backend.GameEntity;
import javafx.scene.image.Image;

public class BackgroundTexture extends GameEntity {
    protected Image image;

    public BackgroundTexture(int x, int y){
        super(x, y);
        isWalkable = true;
        zIndex = 0;
    }

    public BackgroundTexture(int x, int y, int width, int height, Image image) {
        super(x, y, width, height);
        this.image = image;
    }
}
