/*
  Набої для автомата
 */

package backend.triggeredZones;

public class AR_Ammunition extends Detector {
    private int number; // кількість набоїв

    public AR_Ammunition(int x, int y, int number) {
        super(x, y);
        this.number = number;

        width = 40;
        height = 40;
    }
}
