package backend;

import backend.ui.CutsceneState;
import backend.ui.FinalCutsceneState;
import backend.ui.MainMenuState;
import backend.ui.PlayingState;
import backend.ui.StateManager;
import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import java.io.File;

public class LevelLauncher extends Application {

    public static StateManager stateManager;
    private static int currentLevelNumber = 1;

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


        new CameraWindow(width, height);
        new LightingManager();
        new SoundManager();
        new Player(0, 0);
        SaveManager.loadGame();

        Controller controller = new Controller(Player.getInstance());
        stateManager = new StateManager(root, canvas, width, height);

        scene.setOnKeyPressed(event -> {
            stateManager.onKeyPressed(event);
            controller.handleKeyPressed(event);
        });
        scene.setOnKeyReleased(controller::handleKeyReleased);

        // --- ТИМЧАСОВИЙ КОД ДЛЯ ТЕСТУВАННЯ (ЗАКОМЕНТОВАНО) ---
        // stateManager.changeState(new FinalCutsceneState(stateManager));
        // ----------------------------------------------------

        // --- СТАНДАРТНИЙ ЗАПУСК ---
        stateManager.changeState(new MainMenuState(stateManager));
        // -------------------------

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }
                double deltaTime = (now - lastTime) / 1_000_000_000.0;
                if (deltaTime > 0.05) deltaTime = 0.05;
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

    public static void loadAndPlayLevel(int levelNumber, StateManager manager) {
        if (levelNumber == 1 && !GameProgress.introCutscenePlayed) {
            manager.changeState(new CutsceneState(manager));
        } else {
            loadLevel(levelNumber, manager);
            manager.changeState(new PlayingState(manager));
        }
    }

    public static void restartLevel(StateManager manager) {
        loadAndPlayLevel(currentLevelNumber, manager);
    }

    private static void loadLevel(int levelNumber, StateManager manager) {
        SoundManager.getInstance().stopAll(); // Додано глушіння звуків
        Player.getInstance().reset();
        SaveManager.loadStateForLevel(levelNumber);
        currentLevelNumber = levelNumber;
        try {
            String levelFileName = "src/main/java/gamedesign/levels/Level_" + levelNumber + ".json";
            File levelFile = new File(levelFileName);
            if (!levelFile.exists()) {
                System.err.println("Помилка: Файл рівня не знайдено: " + levelFileName);
                manager.changeState(new MainMenuState(manager));
                return;
            }
            LevelParser.loadLevel(manager, levelFile);
        } catch (Exception e) {
            e.printStackTrace();
            manager.changeState(new MainMenuState(manager));
        }
    }
}
