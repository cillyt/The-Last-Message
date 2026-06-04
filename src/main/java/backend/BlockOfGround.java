
package backend;

import javafx.scene.canvas.GraphicsContext;

public class BlockOfGround extends GameEntity {

    public BlockOfGround (int x, int y, int width, int height){
        super(x, y, width, height);
        isWalkable = false;
        zIndex = 1;
    }


}
