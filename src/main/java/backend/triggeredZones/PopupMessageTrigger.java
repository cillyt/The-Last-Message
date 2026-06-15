package backend.triggeredZones;

import backend.GameEntity;
import backend.ui.UIResources;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class PopupMessageTrigger extends Detector {

    private final String message;
    private boolean showMessage;
    private double messageTimer;
    private static final double MESSAGE_DURATION = 10.0;

    private static PopupMessageTrigger currentMessage = null;

    public PopupMessageTrigger(int x, int y, int width, int height,
                               boolean triggerOnce, String message) {
        super(x, y, width, height);
        this.triggerOnce = triggerOnce;
        this.message = message;
        this.showMessage = false;
        this.messageTimer = 0;
    }

    @Override
    protected void onEnter(GameEntity entity) {
        if (isTriggered && triggerOnce) {
            return;
        }

        if (currentMessage != null && currentMessage != this) {
            currentMessage.hide();
        }
        currentMessage = this;

        isTriggered = true;
        showMessage = true;
        messageTimer = MESSAGE_DURATION;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        if (showMessage) {
            messageTimer -= deltaTime;
            if (messageTimer <= 0) {
                hide();
                if (currentMessage == this) {
                    currentMessage = null;
                }
                if (triggerOnce) {
                    setActive(false); // deactivates the trigger
                }
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (showMessage) {
            double width = gc.getCanvas().getWidth();
            double height = gc.getCanvas().getHeight();

            gc.setFont(UIResources.getFont(24));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFill(Color.WHITE);

            String[] lines = message.split("\\\\n");
            for (int i = 0; i < lines.length; i++) {
                gc.fillText(lines[i], width / 2, height - 120 - (lines.length - 1 - i) * 30);
            }
        }
    }

    public void hide() {
        showMessage = false;
        messageTimer = 0;
    }

    @Override
    protected void onExit(GameEntity entity) {
        // Optional: hide message immediately on exit
        // hide();
        // if (currentMessage == this) {
        //     currentMessage = null;
        // }
    }
}
