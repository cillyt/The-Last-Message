package backend;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class MovingGameEntity extends GameEntity{

    protected enum State {
        STAND,
        GO,
        IN_AIR,
    }
    protected State currentState = State.STAND;


    // Швидкості в px/sec
    protected int gravity = 980;
    protected double speedX;
    protected double startJumpSpeed;
    protected double currentVelocityY;
    protected double currentVelocityX;

    protected int targetJumpHeight;

    protected boolean onGround = true;
    protected boolean facingRight = true;

    //Акумулятори для дробових частин кроку
    protected double subPixelX;
    protected double subPixelY;

    /*
     В нащадках зробити:
     this.targetJumpHeight = кінцева висота
     this.startJumpSpeed = -Math.sqrt(2 * GRAVITY * this.targetJumpHeight);
     */
    public MovingGameEntity(int x, int y) {
        super(x, y);
    }

    public MovingGameEntity(){
        super();
    }
    // Методи руху

    public void moveHorizontally(double deltaTime) {
        if (currentVelocityX == 0) return;

        subPixelX += currentVelocityX * deltaTime;
        int deltaX = (int) subPixelX;

        if (deltaX == 0) return;

        subPixelX -= deltaX;

        int sensorX;
        int sensorY = y + 5;
        int sensorW = (deltaX > 0) ? deltaX : -deltaX;
        int sensorH = height - 10;

        if (deltaX > 0) sensorX = x + width;
        else sensorX = x + deltaX;

        GameEntity collision = collision(sensorX, sensorY, sensorW, sensorH, Level.getCurrentLevel().getBlokingObjects());

        if (collision == null || collision.isWalkable) x += deltaX;
        else {
            if (deltaX > 0) x = collision.getX() - width;
            else x = collision.getX() + collision.getWidth();

            subPixelX = 0;
            currentVelocityX = 0;
        }
    }

    public void moveVertically(double deltaTime) {
        currentVelocityY += gravity * deltaTime;
        subPixelY += currentVelocityY * deltaTime;

        int deltaY = (int) subPixelY;

        if (deltaY == 0) return;

        subPixelY -= deltaY;

        int sensorX = x + 5;
        int sensorW = width - 10;
        int sensorY;
        int sensorH;

        if (deltaY > 0) {
            sensorY = y + height;
            sensorH = deltaY;
        } else {
            sensorY = y + deltaY;
            sensorH = -deltaY;
        }

        GameEntity collision = collision(sensorX, sensorY, sensorW, sensorH, Level.getCurrentLevel().getBlokingObjects());

        boolean wasInAir = !onGround;

        if (collision == null) {
            y += deltaY;
            onGround = false;
        } else {
            if (deltaY > 0) {
                y = collision.getY() - height;
                onGround = true;
                if (wasInAir) onLand();
            } else {
                y = collision.getY() + collision.getHeight();
            }

            currentVelocityY = 0;
            subPixelY = 0;
        }
    }

    protected void onLand(){}

    public GameEntity collision(int qX, int qY, int qW, int qH, List<GameEntity> objects) {
        for (GameEntity obj : objects) {
            if (qX < obj.getX() + obj.getWidth() &&
                    qX + qW > obj.getX() &&
                    qY < obj.getY() + obj.getHeight() &&
                    qY + qH > obj.getY()) {
                return obj;
            }
        }
        return null;
    }

    public void update(double deltaTime) {

        moveVertically(deltaTime);

        if (!onGround && currentState != State.IN_AIR) {
            currentState = State.IN_AIR;
        }

        switch (currentState) {
            case GO:
                moveHorizontally(deltaTime);
                break;

            case IN_AIR:
                moveHorizontally(deltaTime);
                break;

            case STAND:
                currentVelocityX = 0;
                break;
        }
    }
}
