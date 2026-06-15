package backend;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineJoin;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class LightingManager {
    @Getter
    public static LightingManager instance;

    public LightingManager() {
        instance = this;
    }

    // ступінь темряви (0 - світло, 1 - повна пітьма)
    private static final double DARKNESS_OPACITY = 1;

    // на скільки пікселів світло проникає вглиб стін
    private static final int LIGHT_PENETRATION = 35;

    public void renderLighting(GraphicsContext gc) {
        CameraWindow camera = CameraWindow.getInstance();
        Player player = Player.getInstance();

        if (!player.isActive()) return;

        int sourceX = player.getX() + player.getWidth() / 2 - camera.getX();
        int sourceY = player.getY() + player.getEyeH() - camera.getY();

        List<Point> polygonPoints = calculateVisibilityPolygon(sourceX, sourceY);

        gc.setFillRule(FillRule.EVEN_ODD);
        gc.setFill(Color.rgb(0, 0, 0, DARKNESS_OPACITY));

        gc.beginPath();

        // Обводимо екран
        double pad = 100;
        gc.moveTo(-pad, -pad);
        gc.lineTo(camera.getScreenWidth() + pad, -pad);
        gc.lineTo(camera.getScreenWidth() + pad, camera.getScreenHeight() + pad);
        gc.lineTo(-pad, camera.getScreenHeight() + pad);
        gc.lineTo(-pad, -pad);

        // Вирізаємо світло
        if (!polygonPoints.isEmpty()) {
            gc.moveTo(polygonPoints.getFirst().x, polygonPoints.getFirst().y);
            for (int i = 1; i < polygonPoints.size(); i++) {
                gc.lineTo(polygonPoints.get(i).x, polygonPoints.get(i).y);
            }
            gc.lineTo(polygonPoints.getFirst().x, polygonPoints.getFirst().y);
        }

        gc.fill();
        gc.closePath();

        // кастомний блюр
        gc.setLineJoin(StrokeLineJoin.ROUND);

        gc.setStroke(Color.rgb(0, 0, 0, DARKNESS_OPACITY * 0.4));
        gc.setLineWidth(15);
        gc.stroke();

        gc.setStroke(Color.rgb(0, 0, 0, DARKNESS_OPACITY * 0.15));
        gc.setLineWidth(35);
        gc.stroke();

        gc.setStroke(Color.rgb(0, 0, 0, DARKNESS_OPACITY * 0.05));
        gc.setLineWidth(60);
        gc.stroke();

        gc.closePath();
    }

    private List<Point> calculateVisibilityPolygon(double sourceX, double sourceY) {
        CameraWindow camera = CameraWindow.getInstance();
        int cameraX = camera.getX();
        int cameraY = camera.getY();
        int screenWidth = camera.getScreenWidth();
        int screenHeight = camera.getScreenHeight();

        List<GameEntity> blocks = Level.getCurrentLevel().getBlocksAndCashes().stream()
                .filter(block -> block.inCamera).toList();

        List<Point> targetPoints = new ArrayList<>();
        List<LineSegment> segments = new ArrayList<>();

        // обмеження огляду
        double marginX = screenWidth * 0.05;
        double marginY = screenHeight * 0.05;

        double minX = marginX;
        double minY = marginY;
        double maxX = screenWidth - marginX;
        double maxY = screenHeight - marginY;

        targetPoints.add(new Point(minX, minY));
        targetPoints.add(new Point(maxX, minY));
        targetPoints.add(new Point(maxX, maxY));
        targetPoints.add(new Point(minX, maxY));

        segments.add(new LineSegment(minX, minY, maxX, minY));
        segments.add(new LineSegment(maxX, minY, maxX, maxY));
        segments.add(new LineSegment(maxX, maxY, minX, maxY));
        segments.add(new LineSegment(minX, maxY, minX, minY));

        for (GameEntity block : blocks) {
            int blockX = block.getX() - cameraX;
            int blockY = block.getY() - cameraY;
            int blockWidth = block.getWidth();
            int blockHeight = block.getHeight();

            double x1 = blockX;
            double y1 = blockY;
            double x2 = blockX + blockWidth;
            double y2 = blockY;
            double x3 = blockX + blockWidth;
            double y3 = blockY + blockHeight;
            double x4 = blockX;
            double y4 = blockY + blockHeight;

            targetPoints.add(new Point(x1, y1));
            targetPoints.add(new Point(x2, y2));
            targetPoints.add(new Point(x3, y3));
            targetPoints.add(new Point(x4, y4));

            segments.add(new LineSegment(x1, y1, x2, y2));
            segments.add(new LineSegment(x2, y2, x3, y3));
            segments.add(new LineSegment(x3, y3, x4, y4));
            segments.add(new LineSegment(x4, y4, x1, y1));
        }

        List<Double> angles = new ArrayList<>();
        for (Point p : targetPoints) {
            double baseAngle = Math.atan2(p.y - sourceY, p.x - sourceX);
            angles.add(baseAngle - 0.0001);
            angles.add(baseAngle);
            angles.add(baseAngle + 0.0001);
        }
        angles.sort(Double::compare);

        List<Point> visibilityPoints = new ArrayList<>();
        for (double angle : angles) {
            double rayDirectionX = Math.cos(angle);
            double rayDirectionY = Math.sin(angle);

            Point closestIntersect = null;
            double minDistance = Double.MAX_VALUE;

            for (LineSegment segment : segments) {
                Point intersect = getIntersection(sourceX, sourceY, rayDirectionX, rayDirectionY, segment);
                if (intersect != null) {
                    double deltaX = intersect.x - sourceX;
                    double deltaY = intersect.y - sourceY;
                    double distanceSquared = deltaX * deltaX + deltaY * deltaY;

                    if (distanceSquared < minDistance) {
                        minDistance = distanceSquared;
                        closestIntersect = intersect;
                    }
                }
            }

            if (closestIntersect != null) {
                // ТРЮК: Проштовхуємо точку перетину всередину стіни вздовж напрямку променя
                closestIntersect.x += rayDirectionX * LIGHT_PENETRATION;
                closestIntersect.y += rayDirectionY * LIGHT_PENETRATION;

                visibilityPoints.add(closestIntersect);
            }
        }

        return visibilityPoints;
    }

    private Point getIntersection(double rx, double ry, double rdx, double rdy, LineSegment segment) {
        double sx = segment.x1;
        double sy = segment.y1;
        double sdx = segment.x2 - segment.x1;
        double sdy = segment.y2 - segment.y1;

        double denom = rdx * sdy - rdy * sdx;
        if (denom == 0) return null;

        double t = ((sx - rx) * sdy - (sy - ry) * sdx) / denom;
        double u = ((sx - rx) * rdy - (sy - ry) * rdx) / denom;

        if (t > 0 && u >= 0 && u <= 1) {
            return new Point(rx + rdx * t, ry + rdy * t);
        }
        return null;
    }

    public static class LineSegment {
        double x1, y1, x2, y2;
        public LineSegment(double x1, double y1, double x2, double y2) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        }
    }

    public static class Point {
        public double x, y;
        public Point(double x, double y) {
            this.x = x; this.y = y;
        }
    }
}