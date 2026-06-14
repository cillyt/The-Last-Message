package backend.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class GameOverState implements GameState {
    private final StateManager manager;
    private VBox menuBox;

    public GameOverState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        menuBox = new VBox(24);
        menuBox.setAlignment(Pos.CENTER);

        Button retry = new Button("ПОЧАТИ ЗАНОВО");
        Button toMenu = new Button("ВИХІД В МЕНЮ");

        double btnWidth = 320;
        double btnHeight = 52;
        String btnStyle = "-fx-background-color: #1a5276; -fx-text-fill: white; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 20 8 20; -fx-background-radius: 18; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #0d2847; -fx-text-fill: white; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 20 8 20; -fx-background-radius: 18; -fx-cursor: hand;";

        retry.setStyle(btnStyle);
        toMenu.setStyle(btnStyle);

        retry.setPrefSize(btnWidth, btnHeight);
        toMenu.setPrefSize(btnWidth, btnHeight);

        retry.setFont(UIResources.getFont(20));
        toMenu.setFont(UIResources.getFont(20));

        retry.setOnMouseEntered(e -> retry.setStyle(btnHoverStyle));
        retry.setOnMouseExited(e -> retry.setStyle(btnStyle));
        toMenu.setOnMouseEntered(e -> toMenu.setStyle(btnHoverStyle));
        toMenu.setOnMouseExited(e -> toMenu.setStyle(btnStyle));

        retry.setOnAction(e -> manager.changeState(new PlayingState(manager)));
        toMenu.setOnAction(e -> manager.changeState(new MainMenuState(manager)));

        menuBox.getChildren().addAll(retry, toMenu);
        manager.getRootPane().getChildren().add(menuBox);
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.setFont(UIResources.getFont(48));
        gc.fillText("YOU DIED", width / 2.0 - 120, height / 3.0);
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(menuBox);
    }
}
