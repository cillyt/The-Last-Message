package backend.ui;

import backend.GameProgress;
import backend.Level;
import backend.LevelLauncher;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class LevelCompleteState implements GameState {
    private final StateManager manager;
    private VBox menuBox;

    public LevelCompleteState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        menuBox = new VBox(24);
        menuBox.setAlignment(Pos.CENTER);

        // Оновлюємо прогрес
        int completedLevel = Level.getCurrentLevel().getLevelNumber();
        int nextLevel = completedLevel + 1;
        if (nextLevel > GameProgress.maxLevelReached) {
            GameProgress.maxLevelReached = nextLevel;
        }

        Button next = new Button("НАСТУПНИЙ РІВЕНЬ");
        Button toMenu = new Button("В МЕНЮ");

        double btnWidth = 320;
        double btnHeight = 52;
        String btnStyle = "-fx-background-color: #ffffff; -fx-text-fill: black; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #ffffff; -fx-text-fill: #4d79ff; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand; -fx-effect: dropshadow(Gaussian, #4d79ff, 20, 0.5, 0, 0);;";

        next.setStyle(btnStyle);
        toMenu.setStyle(btnStyle);

        next.setPrefSize(btnWidth, btnHeight);
        toMenu.setPrefSize(btnWidth, btnHeight);

        next.setFont(UIResources.getFont(20));
        toMenu.setFont(UIResources.getFont(20));

        next.setOnMouseEntered(e -> next.setStyle(btnHoverStyle));
        next.setOnMouseExited(e -> next.setStyle(btnStyle));
        toMenu.setOnMouseEntered(e -> toMenu.setStyle(btnHoverStyle));
        toMenu.setOnMouseExited(e -> toMenu.setStyle(btnStyle));

        next.setOnAction(e -> LevelLauncher.loadAndPlayLevel(nextLevel, manager));
        toMenu.setOnAction(e -> manager.changeState(new MainMenuState(manager)));

        menuBox.getChildren().addAll(next, toMenu);
        manager.getRootPane().getChildren().add(menuBox);
    }

    @Override
    public void update(double deltaTime) {}

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        gc.setFill(Color.web("#062030"));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.setFont(UIResources.getFont(48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("LEVEL COMPLETE", width / 2.0, height / 3.0);
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(menuBox);
    }
}
