package backend.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class PauseState implements GameState {
    private final StateManager manager;
    private final GameState previous;
    private VBox menuBox;

    public PauseState(StateManager manager, GameState previous) {
        this.manager = manager;
        this.previous = previous;
    }

    @Override
    public void enter() {
        menuBox = new VBox(24);
        menuBox.setAlignment(Pos.CENTER);

        Button resume = new Button("ПРОДОВЖИТИ");
        Button toMenu = new Button("ВИХІД В МЕНЮ");

        double btnWidth = 320;
        double btnHeight = 52;
        String btnStyle = "-fx-background-color: #ffffff; -fx-text-fill: black; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand;";
        String btnHoverStyle = "-fx-background-color: #ffffff; -fx-text-fill: #4d79ff; " + UIResources.getFontCSS() + " -fx-font-size: 24px; -fx-padding: 10 30 10 30; -fx-background-radius: 18; -fx-cursor: hand; -fx-effect: dropshadow(Gaussian, #4d79ff, 20, 0.5, 0, 0);;";
        resume.setStyle(btnStyle);
        toMenu.setStyle(btnStyle);

        resume.setPrefSize(btnWidth, btnHeight);
        toMenu.setPrefSize(btnWidth, btnHeight);

        resume.setFont(UIResources.getFont(20));
        toMenu.setFont(UIResources.getFont(20));

        resume.setOnMouseEntered(e -> resume.setStyle(btnHoverStyle));
        resume.setOnMouseExited(e -> resume.setStyle(btnStyle));
        toMenu.setOnMouseEntered(e -> toMenu.setStyle(btnHoverStyle));
        toMenu.setOnMouseExited(e -> toMenu.setStyle(btnStyle));

        resume.setOnAction(e -> manager.changeState(previous));
        toMenu.setOnAction(e -> manager.changeState(new MainMenuState(manager)));

        menuBox.getChildren().addAll(resume, toMenu);
        manager.getRootPane().getChildren().add(menuBox);
    }

    @Override
    public void update(double deltaTime) {
        // do not advance game logic while paused
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        // draw the previous state's render as backdrop if possible
        if (previous != null) previous.render(gc, width, height);

        // darken overlay
        gc.setFill(Color.rgb(0,0,0,0.5));
        gc.fillRect(0, 0, width, height);
    }

    @Override
    public void exit() {
        manager.getRootPane().getChildren().remove(menuBox);
    }
}
