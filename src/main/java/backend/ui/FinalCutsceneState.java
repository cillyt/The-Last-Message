package backend.ui;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.TextAlignment;

public class FinalCutsceneState implements GameState {

    private final StateManager manager;
    private double timer;

    // Ресурси
    private final Image background = new Image("file:assets_new/ControlPanelk.png");
    private final Image docAvatar = new Image("file:assets_new/doc_cutscene/doc_calm.png");
    private final Image baseAvatar = new Image("file:assets_new/soldier_cutscene/phone_soldier.png");

    // Стан діалогу
    private String currentText;
    private boolean isDocSpeaking;
    private boolean showFinalMessage = false;
    private double finalMessageTimer = 0.0;

    // Таймінги
    private static final double TIME_1 = 5.0;  // Думки лікаря
    private static final double TIME_2 = TIME_1 + 5.0;  // База: Дані отримано...
    private static final double TIME_3 = TIME_2 + 7.0;  // База: За вашу відвагу...
    private static final double TIME_4 = TIME_3 + 6.0;  // База: Очікуйте...
    private static final double TIME_5 = TIME_4 + 5.0;  // Початок фінального екрану
    private static final double TIME_TO_MENU = 10.0; // Час до повернення в меню

    public FinalCutsceneState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        timer = 0;
        currentText = "Надіюся що це все було не дарма і я встиг.";
        isDocSpeaking = true;
    }

    @Override
    public void update(double deltaTime) {
        if (showFinalMessage) {
            finalMessageTimer += deltaTime;
            if (finalMessageTimer >= TIME_TO_MENU) {
                manager.changeState(new MainMenuState(manager));
            }
            return;
        }

        timer += deltaTime;

        if (timer <= TIME_1) {
            isDocSpeaking = true;
            currentText = "Надіюся що це все було не дарма і я встиг.";
        } else if (timer <= TIME_2) {
            isDocSpeaking = false;
            currentText = "Дані отримано, людство буде врятовано.";
        } else if (timer <= TIME_3) {
            isDocSpeaking = false;
            currentText = "За вашу відвагу одразу по вашому поверненні вас буде нагороджено медаллю за героїзм\nвищого ступеню і забезпечено всім найкращим до кінця життя.";
        } else if (timer <= TIME_4) {
            isDocSpeaking = false;
            currentText = "Очікуйте на підкріплення, яке зачистить корабель і доставить вас додому.";
        } else if (timer <= TIME_5) {
            currentText = ""; // Очищуємо текст перед фінальним повідомленням
        } else {
            showFinalMessage = true;
        }
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        if (showFinalMessage) {
            // Фінальний екран
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width, height);

            gc.setFill(Color.WHITE);
            gc.setFont(UIResources.getFont(36));
            gc.setTextAlign(TextAlignment.CENTER);
            String finalMessage = "Так могло б бути, але людство вже програло, відповідь так і не надійшла,\nа це були всього лише його галюцинації.";
            String[] lines = finalMessage.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                gc.fillText(lines[i], width / 2.0, height / 2.0 - 40 + (i * 40));
            }
            return;
        }

        // --- Основна кат-сцена ---
        gc.clearRect(0, 0, width, height);
        gc.drawImage(background, 0, 0, width, height);

        // Затемнення фону
        gc.setFill(new Color(0, 0, 0, 0.5));
        gc.fillRect(0, 0, width, height);

        // --- Блок для тексту (нижня чорна смуга) ---
        double textAreaHeight = 150;
        double textAreaY = height - textAreaHeight;
        gc.setFill(Color.BLACK);
        gc.fillRect(0, textAreaY, width, textAreaHeight);

        // --- Аватари ---
        double avatarDisplaySize = 200;

        double docY = textAreaY - avatarDisplaySize;
        double docX = width * 0.1 - avatarDisplaySize / 2;

        double baseY = textAreaY - avatarDisplaySize;
        double baseX = width * 0.9 - avatarDisplaySize / 2;

        gc.save();
        if (isDocSpeaking) {
            gc.drawImage(docAvatar, docX, docY, avatarDisplaySize, avatarDisplaySize);
        } else {
            gc.setGlobalAlpha(0.7);
            gc.drawImage(docAvatar, docX, docY, avatarDisplaySize, avatarDisplaySize);
            gc.setGlobalAlpha(1.0);
            gc.drawImage(baseAvatar, baseX, baseY, avatarDisplaySize, avatarDisplaySize);
        }
        gc.restore();


        // --- Текст ---
        gc.save();
        gc.setFont(UIResources.getFont(24));
        gc.setTextAlign(TextAlignment.CENTER);

        if (isDocSpeaking) {
            gc.setFont(Font.font(gc.getFont().getFamily(), FontPosture.ITALIC, gc.getFont().getSize()));
            gc.setFill(Color.LIGHTGRAY);
        } else {
            gc.setFill(Color.WHITE);
        }

        String[] lines = currentText.split("\\n");
        double lineHeight = 30;
        double totalTextHeight = lines.length * lineHeight;
        double textStartY = textAreaY + (textAreaHeight - totalTextHeight) / 2 + (lineHeight / 2);
        for (int i = 0; i < lines.length; i++) {
            gc.fillText(lines[i], width / 2.0, textStartY + (i * lineHeight));
        }
        gc.restore();
    }

    @Override
    public void exit() {}
}
