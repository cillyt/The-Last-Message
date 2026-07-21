package backend.ui;

import backend.GameProgress;
import backend.LevelLauncher;
import backend.SoundManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class CutsceneState implements GameState {

    private final StateManager manager;
    private int dialogueStep = 0;

    // --- Асети ---
    private final Image background = new Image(getClass().getResourceAsStream("/assets/cutscene_LVL1_background.png"));
    private final Image docTalk = new Image(getClass().getResourceAsStream("/assets/doc_cutscene/doc_talk.png"));
    private final Image docCalm = new Image(getClass().getResourceAsStream("/assets/doc_cutscene/doc_calm.png"));
    private final Image docSadOpen = new Image(getClass().getResourceAsStream("/assets/doc_cutscene/doc_sad_eyesopen.png"));
    private final Image docSadClosed = new Image(getClass().getResourceAsStream("/assets/doc_cutscene/doc_sad_eyesclosed.png"));
    private final Image soldierTalk = new Image(getClass().getResourceAsStream("/assets/soldier_cutscene/soldier_talk.png"));
    private final Image soldierSad = new Image(getClass().getResourceAsStream("/assets/soldier_cutscene/soldier_sad.png"));

    // --- Поточний стан ---
    private String currentText;
    private Image docCurrentAvatar;
    private Image soldierCurrentAvatar;
    private boolean isDocSpeaking;
    private boolean isFadingOut = false;
    private double fadeTimer = 0.0;
    private static final double FADE_DURATION = 1.5;

    public CutsceneState(StateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter() {
        SoundManager.getInstance().stopMusic();
        updateDialogue();
    }

    @Override
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            if (isFadingOut) return;

            dialogueStep++;
            if (dialogueStep < 6) {
                updateDialogue();
            } else {
                isFadingOut = true;
            }
        }
    }

    private void updateDialogue() {
        switch (dialogueStep) {
            case 0:
                isDocSpeaking = false;
                currentText = "Док!";
                docCurrentAvatar = docCalm;
                soldierCurrentAvatar = soldierTalk;
                break;
            case 1:
                isDocSpeaking = true;
                currentText = "Ще поранений? Не рухайся, я допоможу...";
                docCurrentAvatar = docTalk;
                soldierCurrentAvatar = soldierSad;
                break;
            case 2:
                isDocSpeaking = false;
                currentText = "Стій... Часу мало... Слухай уважно! На нашому кораблі сталось вороже вторгнення.\nЇх... Їх набагато більше ніж ми могли б очікувити...\nбільшість особовогу складу вже загинули, а іншим залишилось не довго.";
                soldierCurrentAvatar = soldierTalk;
                docCurrentAvatar = docSadOpen;
                break;
            case 3:
                isDocSpeaking = false;
                docCurrentAvatar = docSadClosed;
                break;
            case 4:
                isDocSpeaking = true;
                currentText = "ЩО!?";
                docCurrentAvatar = docTalk;
                soldierCurrentAvatar = soldierSad;
                break;
            case 5:
                isDocSpeaking = false;
                currentText = "Я теж довго не протримаюсь. У мене флешка з важливою... стратегічною інформацією,\nяка допоможе нам перемогти у війні з цими потворами. Її потрібно доставити в пункт управління,\nта передати інформацію з неї на базу. Мене уже не врятувати... Йди! Ти наша остання... надія... *Помирає*";
                docCurrentAvatar = docSadClosed;
                soldierCurrentAvatar = soldierTalk;
                break;
        }
    }

    @Override
    public void update(double deltaTime) {
        if (isFadingOut) {
            fadeTimer += deltaTime;
            if (fadeTimer >= FADE_DURATION) {
                GameProgress.introCutscenePlayed = true;
                LevelLauncher.loadAndPlayLevel(1, manager);
            }
        }
    }

    @Override
    public void render(GraphicsContext gc, int width, int height) {
        gc.clearRect(0, 0, width, height);
        gc.drawImage(background, 0, 0, width, height);
        gc.setFill(new Color(0, 0, 0, 0.5));
        gc.fillRect(0, 0, width, height);

        double textAreaHeight = 150;
        double textAreaY = height - textAreaHeight;
        gc.setFill(Color.BLACK);
        gc.fillRect(0, textAreaY, width, textAreaHeight);

        // Аватари
        double avatarDisplaySize = 250;
        double docY = height / 2.0 + 50;
        double docX = width * 0.15;
        double soldierY = height / 2.0 + 50;
        double soldierX = width * 0.85 - avatarDisplaySize;

        gc.save();
        gc.setGlobalAlpha(isDocSpeaking ? 1.0 : 0.7);
        if (docCurrentAvatar != null) gc.drawImage(docCurrentAvatar, docX, docY, avatarDisplaySize, avatarDisplaySize);
        gc.setGlobalAlpha(isDocSpeaking ? 0.7 : 1.0);
        if (soldierCurrentAvatar != null) gc.drawImage(soldierCurrentAvatar, soldierX, soldierY, avatarDisplaySize, avatarDisplaySize);
        gc.restore();

        // Текст
        gc.save();
        gc.setFont(UIResources.getFont(24));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
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

        if (isFadingOut) {
            double alpha = Math.min(1.0, fadeTimer / FADE_DURATION);
            gc.setFill(new Color(0, 0, 0, alpha));
            gc.fillRect(0, 0, width, height);
        }
    }

    @Override
    public void exit() {}
}
