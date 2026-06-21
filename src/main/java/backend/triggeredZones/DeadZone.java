/*
  Наносить максимальну шкоду гравцю чи монстрам, які в неї потрапили
 */

package backend.triggeredZones;

import backend.GameEntity;
import backend.Level;
import backend.Player;
import backend.monsters.Monster;

public class DeadZone extends Detector{

    public DeadZone(int x, int y, int width, int height) {
        super(x, y, width, height);

        triggerOnce = false;
    }

    @Override
    protected void onEnter(GameEntity entity) {
        if(entity instanceof Player player){
            player.takeDamage(10000);
        }
        if(entity instanceof Monster monster){
            monster.takeDamage(10000);
        }
    }

    @Override
    public void initialTargetList() {
        targetObjects = Level.getCurrentLevel().getLivingEntitties();
    }
}
