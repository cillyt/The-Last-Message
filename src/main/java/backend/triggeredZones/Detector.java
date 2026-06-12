/*
  Загальний об'єкт що реагує на потрапляння в зону об'єкта і робить щось
  Великі конструктори призначені для унікальних зон, більш часті зроблені окремими класами
*/

package backend.triggeredZones;

import backend.GameEntity;
import backend.Player;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Detector extends GameEntity {

    protected Player targetPlayer;
    protected List<GameEntity> targetObjects;

    protected Runnable onTriggerAction;
    protected boolean isTriggered;
    protected boolean triggerOnce;

    protected boolean havePeriodicActoin; // чи має дію, яка здіснюється через певний період
    protected double timePeriod;
    protected double currentTime;

    // колекція для відстеження всіх об'єктів, які зараз знаходяться в зоні
    protected Set<GameEntity> entitiesInside = new HashSet<>();

    public Detector (int x, int y){
        super(x, y);
        isWalkable = true;
        targetPlayer = Player.getInstance();
    }

    public Detector (int x, int y, int width, int height){
        super(x, y, width, height, true);
        isWalkable = true;
        targetPlayer = Player.getInstance();
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

    protected void doPeriodicAction() {

    }

    // Пусті методи для перевизначення у конкретних зонах
    protected void onEnter(GameEntity entity) {}

    protected void onStay(GameEntity entity, double deltaTime) {}

    protected void onExit(GameEntity entity) {}


    protected boolean checkCollision(GameEntity obj) {
        return this.x < obj.getX() + obj.getWidth() &&
                this.x + this.width > obj.getX() &&
                this.y < obj.getY() + obj.getHeight() &&
                this.y + this.height > obj.getY();
    }

    private void processEntity(GameEntity entity, double deltaTime) {
        if (entity == null) return;

        boolean isColliding = entity.isActive() && checkCollision(entity);
        boolean wasInside = entitiesInside.contains(entity);

        if (isColliding && !wasInside) {
            entitiesInside.add(entity);
            onEnter(entity);
            executeTrigger();
        }
        else if (isColliding && wasInside) {
            onStay(entity, deltaTime);
        }
        else if (!isColliding && wasInside) {
            entitiesInside.remove(entity);
            onExit(entity);
        }
    }

    public void update(double deltaTime) {
        if (isTriggered && triggerOnce) return;

        if (havePeriodicActoin) {
            currentTime += deltaTime;
            if (currentTime > timePeriod) {
                currentTime -= timePeriod;
                doPeriodicAction();
            }
        }

        if (targetPlayer != null) {
            processEntity(targetPlayer, deltaTime);
        }

        if (targetObjects != null) {
            for (GameEntity obj : targetObjects) {
                processEntity(obj, deltaTime);
            }
        }
    }



    public void initialTargetList() {

    }
}