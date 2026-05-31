package backend;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public abstract class MovingGameEntity extends GameEntity{

    enum State {
        STAND,
        GO,
        IN_AIR,
    }
    protected State currentState = State.STAND;

    // Швидкості в px/sec
    protected static final int GRAVITY = 980;
    protected double speedX;
    protected double startJumpSpeed;
    protected double currentVelocityY;
    protected double currentVelocityX;

    protected int targetJumpHeight;

    protected boolean onGround;

    //Акумулятори для дробових частин кроку
    protected double subPixelX;
    protected double subPixelY;

    protected double currentTimeX;
    protected double timePeriod;

    /*
     В нащадках зробити:
     this.targetJumpHeight = кінцева висота
     this.startJumpSpeed = -Math.sqrt(2 * GRAVITY * this.targetJumpHeight);
     */
    public MovingGameEntity(int x, int y) {
        super(x, y);
    }


    // Методи руху
    // ВАЖЛИВО відсортувати список, щоб у ньому об'єкти isWalkable були спочатку

    public void moveHorizontally(double deltaTime, List<GameEntity> objects) {
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

        GameEntity collision = collision(sensorX, sensorY, sensorW, sensorH, objects);

        if (collision == null || collision.isWalkable) x += deltaX;
        else {
            if (deltaX > 0) x = collision.getX() - width;
            else x = collision.getX() + collision.getWidth();

            subPixelX = 0;
            currentVelocityX = 0;
        }
    }

    public void moveVertically(double deltaTime, List<GameEntity> objects) {
        currentVelocityY += GRAVITY * deltaTime;
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

        GameEntity collision = collision(sensorX, sensorY, sensorW, sensorH, objects);

        if (collision == null || collision.isWalkable) {
            y += deltaY;
            onGround = false;
        } else {
            if (deltaY > 0) {
                y = collision.getY() - height;
                onGround = true;
            } else {
                y = collision.getY() + collision.getHeight();
            }

            currentVelocityY = 0;
            subPixelY = 0;
        }


    }

    public void jump(int moveDirection) {
        if (onGround) {
            currentVelocityY = startJumpSpeed;
            currentVelocityX = moveDirection * speedX;
            currentState = State.IN_AIR;
            onGround = false;
        }
    }


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

    public void update(double deltaTime, List<GameEntity> objects) {

        moveVertically(deltaTime, objects);

        if (!onGround && currentState != State.IN_AIR) {
            currentState = State.IN_AIR;
        }

        switch (currentState) {
            case GO:
                moveHorizontally(deltaTime, objects);

                currentTimeX += deltaTime;
                if (currentTimeX >= timePeriod) {
                    currentTimeX -= timePeriod;
                }
                break;

            case IN_AIR:
                moveHorizontally(deltaTime, objects);
                break;

            case STAND:
                currentVelocityX = 0;
                break;
        }
    }

    public void render(GraphicsContext gc) {

    }

}
