package backend.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

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

        Button next = new Button("НАСТУПНИЙ РІВЕНЬ");
        Button toMenu = new Button("В МЕНЮ");

        double btnWidth = 320;
        double btnHeight = 52;
        String btnStyle = "-fx-background-color: #1a5276; -fx-text-fill: white; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 20 8 20; -fx-background-radius: 18; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #0d2847; -fx-text-fill: white; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 20 8 20; -fx-background-radius: 18; -fx-cursor: hand;";

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

        next.setOnAction(e -> { /* TODO: load next level */ });
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
        gc.fillText("LEVEL COMPLETE", width / 2.0 - 200, height / 3.0);
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(menuBox);
    }
}
