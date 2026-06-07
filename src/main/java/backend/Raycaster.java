/*
  Інтерфейс, який пускає промені і перевіряє, чи є на ньому об'єкти
 */

package backend;

import java.util.List;

public interface Raycaster {

    default boolean isObjectOnTheLine(int startX, int startY, int targetX, int targetY, List<? extends GameEntity> objects) {
        for (GameEntity obj : objects) {
            if (lineIntersectsRect(startX, startY, targetX, targetY, obj)) {
                return true;
            }
        }
        return false;
    }

    private boolean lineIntersectsRect(double x1, double y1, double x2, double y2, GameEntity object) {
        double objX = object.getX();
        double objY = object.getY();
        double objW = object.getWidth();
        double objH = object.getHeight();

        // перетин граней
        return linesIntersect(x1, y1, x2, y2, objX, objY, objX, objY + objH) ||
                linesIntersect(x1, y1, x2, y2, objX + objW, objY, objX + objW, objY + objH) ||
                linesIntersect(x1, y1, x2, y2, objX, objY, objX + objW, objY) ||
                linesIntersect(x1, y1, x2, y2, objX, objY + objH, objX + objW, objY + objH);
    }

    // перетин ліній a і b
    private boolean linesIntersect(double ax1, double ay1, double ax2, double ay2,
                                   double bx1, double by1, double bx2, double by2) {
        double denom = (by2 - by1) * (ax2 - ax1) - (bx2 - bx1) * (ay2 - ay1);
        if (denom == 0) return false;

        double ua = ((bx2 - bx1) * (ay1 - by1) - (by2 - by1) * (ax1 - bx1)) / denom;
        double ub = ((ax2 - ax1) * (ay1 - by1) - (ay2 - ay1) * (ax1 - bx1)) / denom;

        return (ua >= 0.0 && ua <= 1.0 && ub >= 0.0 && ub <= 1.0);
    }
}