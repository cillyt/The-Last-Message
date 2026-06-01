/*
  Протагоніст
 */

package backend;

import lombok.Getter;

import java.util.List;

@Getter
public class Player extends MovingGameEntity{

    private int defaultHeight = 170;
    private int heightInCrouch = 100;

    private int speedXinCrouch = 100;

    private boolean isCrouching = false;

    private int maxHp = 100;
    private int currentHp = 100;

    public boolean wantToMoveRight = false;
    public boolean wantToMoveLeft = false;


    /*
      weaponNumber - кількість зброї у гравця на руках
      0 - нічого не підібрано (тільки стартовий пістолет)
      1 - автомат
      2 - дробовик (якщо реалузуємо)
      3 - лазган (якщо реалізуємо)
     */
    private int weaponNumber =0;
    private int currentWeapon = 0;     // поточна зброя, логіка та сама


    public Player(int x, int y) {
        super(x, y);

        height = 170;
        width = 50;

        speedX = 200;
        this.targetJumpHeight = 120;
        this.startJumpSpeed = -Math.sqrt(2 * GRAVITY * this.targetJumpHeight);

        currentVelocityX = 0;
        currentVelocityY = 0;
    }

     // Методи переходу в різні стани

    public void standUp (){
        if (!onGround) return;

        if (isCrouching && !canStand()) return;

        height = defaultHeight;
        y -= defaultHeight - heightInCrouch;
        currentState = State.STAND;
        isCrouching = false;
    }

    public void stop() {
        currentVelocityX = 0;
        if (onGround) {
            currentState = State.STAND;
        }
    }

    public void go() {
        if (!onGround) return;

        if (isCrouching) currentVelocityX = speedXinCrouch;
        else currentVelocityX = speedX;

        if (!facingRight) currentVelocityX *= -1;

        currentState = State.GO;
    }

    @Override
    public void jump() {
        if (isCrouching && canStand()) standUp();

        if (onGround && !isCrouching) {
            currentVelocityY = startJumpSpeed;
            currentState = State.IN_AIR;
            onGround = false;
        }
    }

    public void crouch (){
        if (onGround){
            height = heightInCrouch;
            isCrouching = true;
            y += defaultHeight - heightInCrouch;
        }

    }

    // Методи взяття різної зброї

    public void takePistol (){
        currentWeapon = 0;
    }

    public void takeAR (){
        if (weaponNumber > 0) currentWeapon = 1;
    }


    private boolean canStand() {
        int heightDiff = defaultHeight - heightInCrouch;
        int testY = this.y - heightDiff;

        GameEntity object = collision(this.x, testY, this.width, heightDiff, Level.getCurrentLevel().getBlokingObjects());

        return object == null || object.isWalkable;
    }
}
