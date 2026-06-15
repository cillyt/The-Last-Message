package backend.ui;

import backend.CameraWindow;
import backend.GameEntity;
import backend.Level;
import backend.LightingManager;
import backend.MovingGameEntity;
import backend.Player;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import backend.weapon.Pistol;
import javafx.scene.paint.Color;

import java.util.List;

public class PlayingState implements GameState {
    private final StateManager manager;
    private Button pauseButton;

    public PlayingState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        // pause button in top-right
        pauseButton = new Button("||");
        String pauseBtnStyle = "-fx-background-color: #ffffff; -fx-text-fill: black; " + UIResources.getFontCSS() + " -fx-font-size: 32px; -fx-padding: 6 16 6 16; -fx-background-radius: 18; -fx-cursor: hand;";
        String pauseBtnHoverStyle = "-fx-background-color: #ffffff; -fx-text-fill: #4d79ff; " + UIResources.getFontCSS() + " -fx-font-size: 32px; -fx-padding: 6 16 6 16; -fx-background-radius: 18; -fx-cursor: hand; -fx-effect: dropshadow(Gaussian, #4d79ff, 20, 0.5, 0, 0);;";
        pauseButton.setStyle(pauseBtnStyle);
        pauseButton.setFont(UIResources.getFont(14));
        pauseButton.setOnMouseEntered(e -> pauseButton.setStyle(pauseBtnHoverStyle));
        pauseButton.setOnMouseExited(e -> pauseButton.setStyle(pauseBtnStyle));
        pauseButton.setOnAction(e -> manager.changeState(new PauseState(manager, this)));
        StackPane.setAlignment(pauseButton, Pos.TOP_RIGHT);
        StackPane.setMargin(pauseButton, new Insets(10, 10, 0, 0));
        manager.getRootPane().getChildren().add(pauseButton);

        // ensure canvas has focus for keyboard
        manager.getCanvas().requestFocus();
    }

    @Override
    public void update(double deltaTime) {
        Level current = Level.getCurrentLevel();
        if (current == null) return;

        for (GameEntity entity : current.getAllObjects()) {
            if (entity instanceof MovingGameEntity) {
                ((MovingGameEntity) entity).update(deltaTime);
            } else if (entity instanceof backend.triggeredZones.Detector) {
                ((backend.triggeredZones.Detector) entity).update(deltaTime);
            }
        }

        for (List<? extends GameEntity> list : current.getLists()){
            list.removeIf(obj -> !obj.isActive());
        }

        CameraWindow.getInstance().update(deltaTime);
        current.update(deltaTime);

        // check player death
        Player p = Player.getInstance();
        if (p != null && p.getCurrentHp() <= 0) {
            manager.changeState(new GameOverState(manager));
        }
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        // draw level (similar to previous launcher logic)
        gc.setFill(Color.web("#696A79"));
        gc.fillRect(0, 0, width, height);

        Level current = Level.getCurrentLevel();
        if (current != null) {
            for (GameEntity entity : current.getAllObjects()) {
                entity.render(gc);
            }
        }

        LightingManager.getInstance().renderLighting(gc);

        // HUD: draw HP bar (red)
        Player p = Player.getInstance();
        if (p != null) {
            int hp = p.getCurrentHp();
            int maxHp = p.getMaxHp();
            double hpPct = Math.max(0.0, Math.min(1.0, (double) hp / (double) maxHp));

            int barWidth = 220;
            int barHeight = 18;
            int x = 20;
            int y = 20;

            // background bar
            gc.setFill(Color.web("#333333"));
            gc.fillRect(x, y, barWidth, barHeight);

            // hp fill (red)
            gc.setFill(Color.web("#D32F2F"));
            gc.fillRect(x, y, barWidth * hpPct, barHeight);

            gc.setFill(Color.WHITE);
            gc.setFont(UIResources.getFont(14));
            gc.fillText(String.format("HP: %d/%d", hp, maxHp), x + 6, y + barHeight - 2);

            // weapon icon and ammo below HP bar (moved from top-right to avoid pause button)
            int weaponX = x;
            int weaponY = y + barHeight + 12;
            int iconSize = 40;

            String weaponKey = p.getCurrentWeapon() != null ? p.getCurrentWeapon().toString() : "none";
            Image icon = UIResources.loadWeaponIcon(weaponKey, iconSize);
            if (icon != null) {
                gc.drawImage(icon, weaponX, weaponY, iconSize, iconSize);
            }

            // ammo text next to icon
            gc.setFill(Color.WHITE);
            gc.setFont(UIResources.getFont(14));
            String ammoDisplay = "";
            if (p.getCurrentWeapon() != null) {
                if (p.getCurrentWeapon() instanceof Pistol) {
                    ammoDisplay = "∞";
                } else {
                    ammoDisplay = String.valueOf(p.getCurrentWeapon().getAmmunitionNumber());
                }
            } else ammoDisplay = "-";

            gc.fillText(ammoDisplay, weaponX + iconSize + 8, weaponY + iconSize / 2 + 5);
        }
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(pauseButton);
    }
}
