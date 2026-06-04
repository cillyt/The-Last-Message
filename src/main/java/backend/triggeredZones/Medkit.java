/*
  Аптечка
 */

package backend.triggeredZones;

import backend.Player;

public class Medkit extends Detector {

    public Medkit(int x, int y) {
        super(x, y);

        width = 40;
        height = 40;

        zIndex = 3;
    }

    public void executeTrigger() {
        if (!isActive) return;

        isTriggered = true;

        Player.getInstance().healHp(20);

        isActive = false;
    }
}
