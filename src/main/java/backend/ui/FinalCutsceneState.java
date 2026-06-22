package backend.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.TextAlignment;

public class FinalCutsceneState implements GameState {

    private final StateManager manager;
    private int dialogueStep = 0;

    // Ресурси
    private final Image background = new Image("file:assets_new/ControlPanelk.png");
    private final Image docAvatar = new Image("file:assets_new/doc_cutscene/doc_calm.png");
    private final Image baseAvatar = new Image("file:assets_new/soldier_cutscene/phone_soldier.png");

    // Стан діалогу
    private String currentText;
    private boolean isDocSpeaking;
    private boolean showFinalMessage = false;

    public FinalCutsceneState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        updateDialogue();
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            dialogueStep++;
            if (dialogueStep <= 4) {
                updateDialogue();
            } else {
                manager.changeState(new CreditsState(manager));
            }
        }
    }

    private void updateDialogue() {
        switch (dialogueStep) {
            case 0:
                isDocSpeaking = true;
                currentText = "Надіюся що це все було не дарма і я встиг.";
                break;
            case 1:
                isDocSpeaking = false;
                currentText = "Дані отримано, людство буде врятовано.";
                break;
            case 2:
                isDocSpeaking = false;
                currentText = "За вашу відвагу одразу по вашому поверненні вас буде нагороджено медаллю за героїзм\nвищого ступеню і забезпечено всім найкращим до кінця життя.";
                break;
            case 3:
                isDocSpeaking = false;
                currentText = "Очікуйте на підкріплення, яке зачистить корабель і доставить вас додому.";
                break;
            case 4:
                showFinalMessage = true;
                currentText = "Так могло б бути, але людство вже програло, відповідь так і не надійшла,\nа це були всього лише його галюцинації.";
                break;
        }
    }

    @Override
    public void update(double deltaTime) {}

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        if (showFinalMessage) {
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, width, height);
            gc.setFill(Color.WHITE);
            gc.setFont(UIResources.getFont(36));
            gc.setTextAlign(TextAlignment.CENTER);
            String[] lines = currentText.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                gc.fillText(lines[i], width / 2.0, height / 2.0 - 40 + (i * 40));
            }
            // Підказка
            gc.setFont(UIResources.getFont(18));
            gc.setFill(Color.GRAY);
            gc.fillText("Натисніть [SPACE] для продовження", width / 2.0, height - 20);
            return;
        }

        gc.clearRect(0, 0, width, height);
        gc.drawImage(background, 0, 0, width, height);
        gc.setFill(new Color(0, 0, 0, 0.5));
        gc.fillRect(0, 0, width, height);

        double textAreaHeight = 150;
        double textAreaY = height - textAreaHeight;
        gc.setFill(Color.BLACK);
        gc.fillRect(0, textAreaY, width, textAreaHeight);

        // Аватари
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

        // Текст
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

        // Підказка
        gc.setFont(UIResources.getFont(18));
        gc.setFill(Color.GRAY);
        gc.fillText("Натисніть [SPACE] для продовження", width / 2.0, height - 20);
        gc.restore();
    }

    @Override
    public void exit() {}
}
