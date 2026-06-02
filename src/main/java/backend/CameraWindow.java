package backend;

import lombok.Getter;

@Getter
public class CameraWindow {
    private int x;
    private int y;
    private int screenWidth;
    private int screenHeight;

    @Getter
    private static CameraWindow instance;

    public CameraWindow (int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        instance = this;
    }

    public void update() {
        Player player = Player.getInstance();
        if (player != null) {
            x = player.getX() - (screenWidth / 2);
            y = player.getY() - (screenHeight / 2);
        }
    }
}