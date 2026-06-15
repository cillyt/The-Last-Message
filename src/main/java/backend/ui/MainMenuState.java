package backend.ui;

import backend.GameProgress;
import backend.LevelLauncher;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
        menuBox = new VBox(24); // larger spacing
        menuBox.setAlignment(Pos.CENTER);

        Button start = new Button("СТАРТ");
        Button settings = new Button("НАЛАШТУВАННЯ");
        Button exit = new Button("ВИХІД");

        double btnWidth = 360;
        double btnHeight = 56;
        String btnStyle = "-fx-background-color: #ffffff; -fx-text-fill: black; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #ffffff; -fx-text-fill: #ff0000; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand; -fx-effect: dropshadow(Gaussian, #ff0000, 20, 0.5, 0, 0);;";

        start.setStyle(btnStyle);
        settings.setStyle(btnStyle);
        exit.setStyle(btnStyle);

        start.setPrefSize(btnWidth, btnHeight);
        settings.setPrefSize(btnWidth, btnHeight);
        exit.setPrefSize(btnWidth, btnHeight);

        start.setFont(UIResources.getFont(22));
        settings.setFont(UIResources.getFont(22));
        exit.setFont(UIResources.getFont(22));

        start.setOnMouseEntered(e -> start.setStyle(btnHoverStyle));
        start.setOnMouseExited(e -> start.setStyle(btnStyle));
        settings.setOnMouseEntered(e -> settings.setStyle(btnHoverStyle));
        settings.setOnMouseExited(e -> settings.setStyle(btnStyle));
        exit.setOnMouseEntered(e -> exit.setStyle(btnHoverStyle));
        exit.setOnMouseExited(e -> exit.setStyle(btnStyle));

        start.setOnAction(e -> {
            if (GameProgress.maxLevelReached > 1) {
                manager.changeState(new LevelSelectState(manager));
            } else {
                LevelLauncher.loadAndPlayLevel(1, manager);
            }
        });
        settings.setOnAction(e -> { /* TODO: show settings */ });
        exit.setOnAction(e -> Platform.exit());

        menuBox.getChildren().addAll(start, settings, exit);

        // add centered menu on top of canvas
        StackPane.setAlignment(menuBox, Pos.CENTER);
        manager.getRootPane().getChildren().add(menuBox);
    }

    @Override
    public void update(double deltaTime) {
        // menu doesn't update game logic
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        // draw background image if available
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
