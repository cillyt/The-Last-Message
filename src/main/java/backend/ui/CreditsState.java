package backend.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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
    private final Image background = new Image("file:assets_new/doc_cutscene/final_background.png");

    public CreditsState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        // Починаємо з тексту за межами екрана (знизу)
        scrollY = manager.getHeight();
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            manager.changeState(new MainMenuState(manager));
        }
    }

    @Override
    public void update(double deltaTime) {
        scrollY -= SCROLL_SPEED * deltaTime;

        // Коли останній рядок зникне з екрана, повертаємося в меню
        double totalTextHeight = credits.length * 40;
        if (scrollY < -totalTextHeight) {
            manager.changeState(new MainMenuState(manager));
        }
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        // Фон
        gc.drawImage(background, 0, 0, width, height);

        // Затемнення
        gc.setFill(new Color(0, 0, 0, 0.7));
        gc.fillRect(0, 0, width, height);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);

        for (int i = 0; i < credits.length; i++) {
            if (credits[i].startsWith("-")) {
                // Менший шрифт для опису
                gc.setFont(UIResources.getFont(20));
            } else {
                // Більший шрифт для імен та заголовка
                gc.setFont(UIResources.getFont(32));
            }
            gc.fillText(credits[i], width / 2.0, scrollY + i * 40);
        }

        // Підказка
        gc.setFont(UIResources.getFont(18));
        gc.setFill(Color.GRAY);
        gc.fillText("Натисніть [SPACE] щоб пропустити", width / 2.0, height - 20);
    }

    @Override
    public void exit() {}
}
