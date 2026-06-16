package backend.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class CreditsState implements GameState {

    private final StateManager manager;
    private double scrollY;
    private final String[] credits = {
            "Розробники:",
            "",
            "Лятамбур Кіра",
            "- пошук і інтегрування асетів, інтерфейс, перемикання анімації.",
            "",
            "Матящук Анна",
            "- рівні, геймплей, поведінка монстрів, баланс.",
            "",
            "Малецький Олександр",
            "- тестування, відеоролик, звіт, інша документація.",
            "",
            "П’ятаченко Гліб",
            "- бекенд і рушій."
    };

    private static final double SCROLL_SPEED = 40.0; // Пікселів за секунду

    public CreditsState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        scrollY = manager.getHeight();
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        // Пропустити титри по натисканню
        manager.changeState(new MainMenuState(manager));
    }

    @Override
    public void update(double deltaTime) {
        scrollY -= SCROLL_SPEED * deltaTime;

        double totalTextHeight = credits.length * 40;
        if (scrollY < -totalTextHeight) {
            manager.changeState(new MainMenuState(manager));
        }
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        for (int i = 0; i < credits.length; i++) {
            if (credits[i].startsWith("-")) {
                gc.setFont(UIResources.getFont(20));
            } else {
                gc.setFont(UIResources.getFont(32));
            }
            gc.fillText(credits[i], width / 2.0, scrollY + i * 40);
        }
    }

    @Override
    public void exit() {}
}
