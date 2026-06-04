/*
  Загальний об'єкт що реагує на потрапляння в зону об'єкта і робить щось
  Великі конструктори призначені для унікальних зон, більш часті зроблені окремими класами
*/

package backend.triggeredZones;

import backend.GameEntity;
import backend.Player;
import javafx.scene.canvas.GraphicsContext;
import java.util.List;

public class Detector extends GameEntity {

    protected Player targetPlayer;
    protected List<GameEntity> targetObjects;

    protected Runnable onTriggerAction;
    protected boolean isTriggered;
    protected boolean triggerOnce;

    public Detector (int x, int y){
        super(x, y);
        isWalkable = true;
    }

    public Detector (int x, int y, int width, int height){
        super(x, y, width, height, true);
    }

    public Detector(int x, int y, int width, int height,
                    boolean triggerOnce, Runnable onTriggerAction) {
        super(x, y, width, height, true);
        this.targetPlayer = Player.getInstance();
        this.triggerOnce = triggerOnce;
        this.onTriggerAction = onTriggerAction;
        this.isTriggered = false;
    }

    public Detector(int x, int y, int width, int height,
                    List<GameEntity> targetObjects, boolean triggerOnce, Runnable onTriggerAction) {
        super(x, y, width, height, true);
        this.targetObjects = targetObjects;
        this.triggerOnce = triggerOnce;
        this.onTriggerAction = onTriggerAction;
        this.isTriggered = false;
    }


    public void executeTrigger() {
        isTriggered = true;
        if (onTriggerAction != null) {
            onTriggerAction.run();
        }
    }

    private boolean checkCollision(GameEntity obj) {
        return this.x < obj.getX() + obj.getWidth() &&
                this.x + this.width > obj.getX() &&
                this.y < obj.getY() + obj.getHeight() &&
                this.y + this.height > obj.getY();
    }

    public void update(double deltaTime) {
        if (isTriggered && triggerOnce) return;

        if (targetPlayer != null && checkCollision(targetPlayer)) {
            executeTrigger();
        } else if (targetObjects != null) {
            for (GameEntity obj : targetObjects) {
                if (checkCollision(obj)) {
                    executeTrigger();
                    break;
                }
            }
        }
    }
}
