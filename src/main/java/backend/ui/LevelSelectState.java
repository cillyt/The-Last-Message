package backend.ui;

import backend.GameProgress;
import backend.LevelLauncher;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class LevelSelectState implements GameState {
    private final StateManager manager;
    private VBox menuBox;

    public LevelSelectState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 40px; -fx-border-radius: 20; -fx-background-radius: 20;");

        // Стилі кнопок
        String btnStyle = "-fx-background-color: #ffffff; -fx-text-fill: black; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 25 8 25; -fx-background-radius: 15; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #ffffff; -fx-text-fill: #4d79ff; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 25 8 25; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(Gaussian, #4d79ff, 15, 0.4, 0, 0);";

        // Створюємо кнопки для кожного доступного рівня
        for (int i = 1; i <= GameProgress.maxLevelReached; i++) {
            final int levelNumber = i;
            Button levelButton = new Button("РІВЕНЬ " + levelNumber);
            levelButton.setStyle(btnStyle);
            levelButton.setPrefSize(300, 50);
            levelButton.setOnMouseEntered(e -> levelButton.setStyle(btnHoverStyle));
            levelButton.setOnMouseExited(e -> levelButton.setStyle(btnStyle));
            levelButton.setOnAction(e -> LevelLauncher.loadAndPlayLevel(levelNumber, manager));
            menuBox.getChildren().add(levelButton);
        }

        // Кнопка "Назад"
        Button backButton = new Button("НАЗАД");
        backButton.setStyle(btnStyle);
        backButton.setPrefSize(300, 50);
        backButton.setOnMouseEntered(e -> backButton.setStyle(btnHoverStyle));
        backButton.setOnMouseExited(e -> backButton.setStyle(btnStyle));
        backButton.setOnAction(e -> manager.changeState(new MainMenuState(manager)));
        menuBox.getChildren().add(backButton);

        manager.getRootPane().getChildren().add(menuBox);
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        // Малюємо фон, щоб було видно, що це меню вибору
        gc.setFill(new Color(0.1, 0.1, 0.2, 0.5));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.setFont(UIResources.getFont(48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("ОБЕРІТЬ РІВЕНЬ", width / 2.0, height / 4.0);
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(menuBox);
    }
}
