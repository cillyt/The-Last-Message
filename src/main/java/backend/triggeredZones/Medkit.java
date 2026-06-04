/*
  Аптечка
 */

package backend.triggeredZones;

public class Medkit extends Detector {

    public Medkit(int x, int y) {
        super(x, y);

        width = 40;
        height = 40;

        zIndex = 3;
    }
}
