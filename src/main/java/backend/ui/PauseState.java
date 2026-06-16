package backend.ui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class PauseState implements GameState {
    private final StateManager manager;
    private final GameState previousState;
    private VBox menuBox;

    public PauseState(StateManager manager, GameState previousState) {
        this.manager = manager;
        this.previousState = previousState;
    }

    @Override
    public void enter() {
        menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 40px; -fx-border-radius: 20; -fx-background-radius: 20;");

        Button resume = new Button("ПРОДОВЖИТИ");
        Button toMenu = new Button("В МЕНЮ");
        Button exit = new Button("ВИХІД");

        String btnStyle = "-fx-background-color: #ffffff; -fx-text-fill: black; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 25 8 25; -fx-background-radius: 15; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #ffffff; -fx-text-fill: #4d79ff; " + UIResources.getFontCSS() + " -fx-font-size: 20px; -fx-padding: 8 25 8 25; -fx-background-radius: 15; -fx-cursor: hand; -fx-effect: dropshadow(Gaussian, #4d79ff, 15, 0.4, 0, 0);";

        resume.setStyle(btnStyle);
        toMenu.setStyle(btnStyle);
        exit.setStyle(btnStyle);

        resume.setPrefSize(300, 50);
        toMenu.setPrefSize(300, 50);
        exit.setPrefSize(300, 50);

        resume.setOnMouseEntered(e -> resume.setStyle(btnHoverStyle));
        resume.setOnMouseExited(e -> resume.setStyle(btnStyle));
        toMenu.setOnMouseEntered(e -> toMenu.setStyle(btnHoverStyle));
        toMenu.setOnMouseExited(e -> toMenu.setStyle(btnStyle));
        exit.setOnMouseEntered(e -> exit.setStyle(btnHoverStyle));
        exit.setOnMouseExited(e -> exit.setStyle(btnStyle));

        resume.setOnAction(e -> manager.changeState(previousState));
        toMenu.setOnAction(e -> manager.changeState(new MainMenuState(manager)));
        exit.setOnAction(e -> Platform.exit());

        menuBox.getChildren().addAll(resume, toMenu, exit);
        manager.getRootPane().getChildren().add(menuBox);
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode().toString().equals("ESCAPE")) {
            manager.changeState(previousState);
        }
    }

    @Override
    public void update(double deltaTime) {
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        if (previousState != null) {
            previousState.render(gc, width, height);
        }
        gc.setFill(new Color(0.1, 0.1, 0.2, 0.5));
        gc.fillRect(0, 0, width, height);

        gc.setFill(Color.WHITE);
        gc.setFont(UIResources.getFont(48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("ПАУЗА", width / 2.0, height / 4.0);
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(menuBox);
    }
}
