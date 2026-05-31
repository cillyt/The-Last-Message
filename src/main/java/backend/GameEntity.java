package backend;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import lombok.Getter;

@Getter
public abstract class GameEntity {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected boolean isWalkable;

    public GameEntity (int x, int y){
        this.x = x;
        this.y = y;
    }


    public GameEntity (int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GameEntity (int x, int y, int width, int height, boolean isWalkable){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isWalkable = isWalkable;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D(x, y, width, height);
    }




}