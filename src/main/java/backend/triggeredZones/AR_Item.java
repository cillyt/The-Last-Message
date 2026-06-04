/*
Автомат, який лежить на землі
Після його підбирання герой матиме можливість їм користуватися
 */

package backend.triggeredZones;

import backend.Player;

public class AR_Item extends Detector {

    public AR_Item(int x, int y) {
        super(x, y);

        width = 50;
        height = 50;

        zIndex = 3;
    }

    public void executeTrigger() {
        if (!isActive) return;

        isTriggered = true;

        Player.getInstance().unlockWeapon(1);
        isActive = false;
    }
}
