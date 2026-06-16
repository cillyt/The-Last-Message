import backend.LevelLauncher;

/**
 * Цей клас є "обгорткою" для запуску, яка вирішує проблему
 * "JavaFX runtime components are missing" при стандартному запуску з IDE.
 *
 * Він успадковує логіку запуску з вашого основного класу LevelLauncher.
 * Тепер це єдина точка входу в програму.
 */
public class Main {
    public static void main(String[] args) {
        LevelLauncher.main(args);
    }
}
