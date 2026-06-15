package backend;

import backend.ui.MainMenuState;
import backend.ui.StateManager;
import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import java.io.File;

public class LevelLauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        int width = (int) screenBounds.getWidth();
        int height = (int) screenBounds.getHeight();

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, width, height);

        new Player(0, 0);
        new CameraWindow(width, height);
        new LightingManager();

        Controller controller = new Controller(Player.getInstance());
        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnKeyReleased(controller::handleKeyReleased);

        try {
            File levelFile = new File("src/main/java/gamedesign/levels/Level_1.json");
            LevelParser.loadLevel(levelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StateManager stateManager = new StateManager(root, canvas, width, height);
        stateManager.changeState(new MainMenuState(stateManager));

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                stateManager.update(deltaTime);
                stateManager.render(gc);
            }
        };

        gameLoop.start();

        primaryStage.setTitle("The Last Message");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
    }
}