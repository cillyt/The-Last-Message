/*
  Декоративна наліпка, яка ставиться поверх фону і землі
  Це може бути кров, потряпина і тп
 */

package backend.background;

import backend.GameEntity;
import javafx.scene.image.Image;

public class Sticker extends GameEntity {
    protected Image image;

    public Sticker(int x, int y){
        super(x, y);
        isWalkable = true;
        zIndex = 2;
    }

    public Sticker(int x, int y, int width, int height, Image image) {
        super(x, y, width, height);
        this.image = image;
    }
}

