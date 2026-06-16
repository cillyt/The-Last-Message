package backend.ui;

import backend.GameProgress;
import backend.LevelLauncher;
import backend.SaveManager;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class MainMenuState implements GameState {
    private final StateManager manager;
    private VBox menuBox;

    public MainMenuState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        menuBox = new VBox(24);
        menuBox.setAlignment(Pos.CENTER);

        Button continueButton = new Button("ПРОДОВЖИТИ");
        Button newGameButton = new Button("НОВА ГРА");
        Button exitButton = new Button("ВИХІД");

        // Стилі
        double btnWidth = 360;
        double btnHeight = 56;
        String btnStyle = "-fx-background-color: #ffffff; -fx-text-fill: black; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #ffffff; -fx-text-fill: #ff0000; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand; -fx-effect: dropshadow(Gaussian, #ff0000, 20, 0.5, 0, 0);;";

        continueButton.setStyle(btnStyle);
        newGameButton.setStyle(btnStyle);
        exitButton.setStyle(btnStyle);

        continueButton.setPrefSize(btnWidth, btnHeight);
        newGameButton.setPrefSize(btnWidth, btnHeight);
        exitButton.setPrefSize(btnWidth, btnHeight);

        continueButton.setOnMouseEntered(e -> continueButton.setStyle(btnHoverStyle));
        continueButton.setOnMouseExited(e -> continueButton.setStyle(btnStyle));
        newGameButton.setOnMouseEntered(e -> newGameButton.setStyle(btnHoverStyle));
        newGameButton.setOnMouseExited(e -> newGameButton.setStyle(btnStyle));
        exitButton.setOnMouseEntered(e -> exitButton.setStyle(btnHoverStyle));
        exitButton.setOnMouseExited(e -> exitButton.setStyle(btnStyle));

        // Логіка кнопок
        continueButton.setOnAction(e -> {
            if (GameProgress.maxLevelReached > 1) {
                manager.changeState(new LevelSelectState(manager));
            } else {
                // Якщо прогресу немає, "Продовжити" запускає перший рівень
                LevelLauncher.loadAndPlayLevel(1, manager);
            }
        });

        newGameButton.setOnAction(e -> {
            SaveManager.resetProgress();
            LevelLauncher.loadAndPlayLevel(1, manager);
        });

        exitButton.setOnAction(e -> Platform.exit());

        menuBox.getChildren().addAll(continueButton, newGameButton, exitButton);
        manager.getRootPane().getChildren().add(menuBox);
    }

    @Override
    public void onKeyPressed(KeyEvent event) {}

    @Override
    public void update(double deltaTime) {}

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        if (UIResources.getBackground() != null) {
            gc.drawImage(UIResources.getBackground(), 0, 0, width, height);
        } else {
            gc.setFill(Color.web("#050814"));
            gc.fillRect(0, 0, width, height);
        }

        gc.setFill(Color.web("#ffffff"));
        gc.setFont(UIResources.getFont(64));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("THE LAST MESSAGE", width / 2.0, height / 4.0);
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(menuBox);
    }
}
