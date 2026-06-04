/*
  Поповнення набоїв для автомата
 */

package backend.triggeredZones;

import backend.Player;
import backend.weapon.AR;
import backend.weapon.Weapon;

public class AR_Ammunition extends Detector {
    private int number; // кількість набоїв

    public AR_Ammunition(int x, int y, int number) {
        super(x, y);
        this.number = number;

        width = 40;
        height = 40;

        zIndex = 3;
    }

    public void executeTrigger() {
        if (!isActive) return;

        isTriggered = true;

        Weapon[] weapons = Player.getInstance().getWeapons();
        for (Weapon weapon : weapons){
            if(weapon instanceof AR) {
                weapon.addAmmunition(number);
                isActive = false;
                return;
            }
        }
    }
}
