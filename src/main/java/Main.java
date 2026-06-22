import backend.LevelLauncher;

/**
 * Обгорткою для запуску, яка вирішує проблему
 * "JavaFX runtime components are missing" при стандартному запуску з IDE.
 */
public class Main {
    public static void main(String[] args) {
        LevelLauncher.main(args);
    }
}
