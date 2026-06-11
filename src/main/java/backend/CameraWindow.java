package backend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CameraWindow {
    public double internalX;
    public double internalY;

    private int screenWidth;
    private int screenHeight;

    private boolean followPlayer = false;
    private double cameraSpeed;

    // Параметри дедзони
    private final int deadZoneWidth = 150;
    private final int deadZoneHeight = 140;
    private int deadZoneBoxX;
    private int deadZoneBoxY;

    private final int lookAheadDistance = 180;

    private double shakeIntensity = 0;
    private double shakeDuration = 0;
    private double shakeOffsetX = 0;
    private double shakeOffsetY = 0;

    @Getter
    private static CameraWindow instance;

    public CameraWindow(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        instance = this;

        Player player = Player.getInstance();

        internalX = player.getX() - (screenWidth / 2.0) + lookAheadDistance;
        internalY = player.getY() - (screenHeight / 2.0);
        cameraSpeed = player.getSpeedX();

        deadZoneBoxX = player.getX() + (player.getWidth() / 2) - (deadZoneWidth / 2);
        deadZoneBoxY = player.getY() + (player.getHeight()) - deadZoneHeight;

    }

    public int getX() {
        return (int) (internalX + shakeOffsetX);
    }

    public int getY() {
        return (int) (internalY + shakeOffsetY);
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
            shakeOffsetX = (Math.random() * 2 - 1) * shakeIntensity;
            shakeOffsetY = (Math.random() * 2 - 1) * shakeIntensity;
        } else {
            shakeOffsetX = 0;
            shakeOffsetY = 0;
        }

        // перевіряємо, чи вийшов гравець за коробку
        if (!followPlayer) {
            double playerCenterX = player.getX() + (player.getWidth() / 2.0);
            double playerCenterY = player.getY() + (player.getHeight() / 2.0);

            boolean isOutsideX = playerCenterX < deadZoneBoxX ||
                    playerCenterX > deadZoneBoxX + deadZoneWidth;

            boolean isOutsideY = playerCenterY < deadZoneBoxY ||
                    playerCenterY > deadZoneBoxY + deadZoneHeight;

            if (isOutsideX || isOutsideY) {
                followPlayer = true;
            }
        }

        // якщо камера прокинулась - вона летить до targetX
        if (followPlayer) {
            double targetX = player.getX() - (screenWidth / 2.0);
            double targetY = player.getY() - (screenHeight / 2.0);

            if (player.isFacingRight()) targetX += lookAheadDistance;
            else targetX -= lookAheadDistance;

            Level level = Level.getCurrentLevel();
            int minCamX = level.getX();
            int maxCamX = Math.max(minCamX, level.getX() + level.getWidth() - screenWidth);
            int minCamY = level.getY();
            int maxCamY = Math.max(minCamY, level.getY() + level.getHeight() - screenHeight);

            if (targetX < minCamX) targetX = minCamX;
            if (targetX > maxCamX) targetX = maxCamX;
            if (targetY < minCamY) targetY = minCamY;
            if (targetY > maxCamY) targetY = maxCamY;

            double deltaX = targetX - internalX;
            double deltaY = targetY - internalY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            if (distance > 1.0) {
                double moveStep = cameraSpeed * deltaTime;

                if (distance > screenWidth / 2.0) moveStep = 1500 * deltaTime;

                if (moveStep >= distance) {
                    internalX = targetX;
                    internalY = targetY;
                } else {
                    internalX += (deltaX / distance) * moveStep;
                    internalY += (deltaY / distance) * moveStep;
                }
            } else {
                internalX = targetX;
                internalY = targetY;
            }

            // оновлення дедзони
            boolean isCameraArrived = Math.abs(targetX - internalX) <= 1.0 && Math.abs(targetY - internalY) <= 1.0;
            boolean isPlayerStopped = player.getCurrentState() == backend.MovingGameEntity.State.STAND;

            if (isCameraArrived && isPlayerStopped) {
                followPlayer = false;

                deadZoneBoxX = player.getX() + (player.getWidth() / 2) - (deadZoneWidth / 2);
                if (player.isOnGround()) {
                    deadZoneBoxY = player.getY() + (player.getHeight() / 2) - (deadZoneHeight / 2);
                }
            }
        }
    }
}