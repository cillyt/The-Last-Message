/*
  Схованка в стіні з приписами чи іншими приколюхами
  Маскується під стіну
  Коли гравець на неї заходить - стає майже прозорою
 */

package backend.triggeredZones;

public class Cashe extends Detector{

    public Cashe(int x, int y, int width, int height) {
        super(x, y, width, height);
        zIndex = 2;
    }
}
