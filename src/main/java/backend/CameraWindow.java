/*
  Проста цілочисельна камера з жорсткою прив'язкою до гравця, обмеженням меж рівня та ефектом тряски
*/
package backend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CameraWindow {
    public int x;
    public int y;

    private int screenWidth;
    private int screenHeight;

    private double shakeIntensity = 0;
    private double shakeDuration = 0;
    private int shakeOffsetX = 0;
    private int shakeOffsetY = 0;

    @Getter
    private static CameraWindow instance;

    public CameraWindow(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        instance = this;

        Player player = Player.getInstance();
        if (player != null) {
            this.x = player.getX() + (player.getWidth() / 2) - (screenWidth / 2);
            this.y = player.getY() + (player.getHeight() / 2) - (screenHeight / 2);
        }
    }

    public int getX() {
        return x + shakeOffsetX;
    }

    public int getY() {
        return y + shakeOffsetY;
    }

    public void applyShake(double intensity, double duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
    }

    public void update(double deltaTime) {
        Player player = Player.getInstance();
        if (player == null) return;

        // тряска
        if (shakeDuration > 0) {
            shakeDuration -= deltaTime;
            shakeOffsetX = (int) ((Math.random() * 2 - 1) * shakeIntensity);
            shakeOffsetY = (int) ((Math.random() * 2 - 1) * shakeIntensity);
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
            shakeIntensity = 0;
        }

        // цільова позиція по центру гравця
        int targetX = player.getX() + (player.getWidth() / 2) - (screenWidth / 2);
        int targetY = player.getY() + (player.getHeight() / 2) - (screenHeight / 2);

        // обмеження камери межами рівня
        Level level = Level.getCurrentLevel();
        int minCamX = level.getX();
        int maxCamX = Math.max(minCamX, level.getX() + level.getWidth() - screenWidth);
        int minCamY = level.getY();
        int maxCamY = Math.max(minCamY, level.getY() + level.getHeight() - screenHeight);

        if (targetX < minCamX) targetX = minCamX;
        if (targetX > maxCamX) targetX = maxCamX;
        if (targetY < minCamY) targetY = minCamY;
        if (targetY > maxCamY) targetY = maxCamY;

        // встановлюємо фінальні координати
        x = targetX;
        y = targetY;
    }
}