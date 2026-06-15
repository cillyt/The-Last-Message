/*
  Схованка в стіні з приписами чи іншими приколюхами
  Маскується під стіну
  Коли гравець на неї заходить - стає майже прозорою
 */

package backend.triggeredZones;

import backend.GameEntity;
import backend.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Cashe extends Detector {

    private boolean isTransparent = false;

    public Cashe(int x, int y, int width, int height, int imageNumber) {
        super(x, y, width, height);
        this.zIndex = 6;

        this.image = new Image(getAssetPath("assets/cashe_textures/cashe" + imageNumber + ".png"));
    }

    @Override
    protected void onEnter(GameEntity entity) {
        if (entity instanceof Player) {
            isTransparent = true;
        }
    }

    @Override
    protected void onExit(GameEntity entity) {
        if (entity instanceof Player) {
            isTransparent = false;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (!isActive) return;

        double oldAlpha = gc.getGlobalAlpha();

        if (isTransparent) gc.setGlobalAlpha(0.3);

        super.render(gc);
        gc.setGlobalAlpha(oldAlpha);
    }
}
