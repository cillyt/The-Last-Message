package backend.triggeredZones;

import backend.GameEntity;

public class PopupMessageTrigger extends Detector{

    public PopupMessageTrigger(int x, int y, int width, int height,
                               boolean triggerOnce, String message) {
        super(x, y, width, height);

        this.triggerOnce = triggerOnce;
    }

    @Override
    protected void onEnter(GameEntity entity) {
         isTriggered = true;

         // логіка виведення повідомлення
    }

    @Override
    protected void onExit(GameEntity entity) {
        // логіка закриття повідомлення (якщо буде потрібно)
    }
}
