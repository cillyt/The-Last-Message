package backend;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import java.io.File;
import java.util.List;

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

        Group root = new Group(canvas);
        Scene scene = new Scene(root, width, height);

        new Player(0, 0);
        new CameraWindow(width, height);

        Controller controller = new Controller(Player.getInstance());
        scene.setOnKeyPressed(controller::handleKeyPressed);
        scene.setOnKeyReleased(controller::handleKeyReleased);

        try {
            File levelFile = new File("src/main/java/gamedesign/levels/Level_0.json");
            LevelParser.loadLevel(levelFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

                update(deltaTime);
                render(gc, width, height);
            }

            private void update(double deltaTime) {

                for (GameEntity entity : Level.getCurrentLevel().getAllObjects()) {
                    if (entity instanceof MovingGameEntity) {
                        ((MovingGameEntity) entity).update(deltaTime);
                    } else if (entity instanceof backend.triggeredZones.Detector) {
                        ((backend.triggeredZones.Detector) entity).update(deltaTime);
                    }
                }

                for (List<GameEntity> list : Level.getCurrentLevel().getLists()){
                    list.removeIf(obj -> !obj.isActive());
                }
            }

            private void render(GraphicsContext gc, int w, int h) {
                gc.setFill(Color.web("#696A79"));
                gc.fillRect(0, 0, w, h);

                CameraWindow.getInstance().update();

                for (GameEntity entity : Level.getCurrentLevel().getAllObjects()) {
                    entity.render(gc);
                }

                Player.getInstance().render(gc);
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