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
    private final int deadZoneHeight = 3000;
    private int deadZoneBoxX;
    private int deadZoneBoxY;

    private final int lookAheadDistance = 180;
    private double smoothLookAhead = 160;

    private double lastPlayerExactX;
    private double lastPlayerExactY;

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
        return (int) Math.round(internalX + shakeOffsetX);
    }

    public int getY() {
        return (int) Math.round(internalY + shakeOffsetY);
    }

    public void applyShake(double intensity, double duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
    }

    public void update(double deltaTime) {
        Player player = Player.getInstance();
        if (player == null) return;

        double currentExactPx = player.getExactX();
        double currentExactPy = player.getExactY();

        double playerDx = currentExactPx - lastPlayerExactX;
        double playerDy = currentExactPy - lastPlayerExactY;

        lastPlayerExactX = currentExactPx;
        lastPlayerExactY = currentExactPy;



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
            int playerCenterX = player.getX() + (player.getWidth() / 2);
            int playerCenterY = player.getY() + (player.getHeight() / 2);

            boolean isOutsideX = playerCenterX < deadZoneBoxX ||
                    playerCenterX > deadZoneBoxX + deadZoneWidth;

            boolean isOutsideY = playerCenterY < deadZoneBoxY ||
                    playerCenterY > deadZoneBoxY + deadZoneHeight;

            if (isOutsideX || isOutsideY) {
                followPlayer = true;
            }
        }

        if (followPlayer) {
            internalX += playerDx;
            internalY += playerDy;

            double targetX = currentExactPx - (screenWidth / 2.0);
            double targetY = currentExactPy - (screenHeight / 2.0);

            double desiredLookAhead = player.isFacingRight() ? lookAheadDistance : -lookAheadDistance;
            smoothLookAhead += (desiredLookAhead - smoothLookAhead) * Math.min(3.0 * deltaTime, 1.0);
            targetX += smoothLookAhead;

            Level level = Level.getCurrentLevel();
            int minCamX = level.getX();
            int maxCamX = Math.max(minCamX, level.getX() + level.getWidth() - screenWidth);
            int minCamY = level.getY();
            int maxCamY = Math.max(minCamY, level.getY() + level.getHeight() - screenHeight);

            if (targetX < minCamX) targetX = minCamX;
            if (targetX > maxCamX) targetX = maxCamX;
            if (targetY < minCamY) targetY = minCamY;
            if (targetY > maxCamY) targetY = maxCamY;

            double diffX = targetX - internalX;
            double diffY = targetY - internalY;

            double t = Math.min(2.5 * deltaTime, 1.0);
            internalX += diffX * t;
            internalY += diffY * t;

            // оновлення дедзони
            boolean isCameraArrived = Math.abs(diffX) <= 2.0 && Math.abs(diffY) <= 2.0;
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


    public double getExactX() {
        return internalX + shakeOffsetX;
    }

    public double getExactY() {
        return internalY + shakeOffsetY;
    }
}