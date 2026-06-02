/*
  Протагоніст
 */

package backend;

import lombok.Getter;

import java.util.List;

@Getter
public class Player extends MovingGameEntity{

    @Getter
    private static Player instance;

    private int defaultHeight = 170;
    private int heightInCrouch = 100;

    private int speedXinCrouch = 100;

    private boolean isCrouching = false;

    private int maxHp = 100;
    private int currentHp = 100;

    private boolean wantToMoveRight = false;
    private boolean wantToMoveLeft = false;
    private boolean wantToCrouch = false;


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

        instance = this;

        height = 170;
        width = 50;

        speedX = 200;
        this.targetJumpHeight = 120;
        this.startJumpSpeed = -Math.sqrt(2 * GRAVITY * this.targetJumpHeight);

        currentVelocityX = 0;
        currentVelocityY = 0;
    }

    // Методи переходу в різні стани

    private void stop() {
        currentVelocityX = 0;
        currentState = State.STAND;
    }

    private void go() {

        if (isCrouching) currentVelocityX = speedXinCrouch;
        else currentVelocityX = speedX;

        if (!facingRight) currentVelocityX *= -1;

        currentState = State.GO;
    }

    @Override
    protected void jump() {
        currentVelocityY = startJumpSpeed;
        currentState = State.IN_AIR;
        onGround = false;
    }

    private void crouch (){
        if (isCrouching) return;
        height = heightInCrouch;
        isCrouching = true;
        y += defaultHeight - heightInCrouch;
    }

    private void standUp (){
        if(!isCrouching) return;
        height = defaultHeight;
        y -= defaultHeight - heightInCrouch;
        isCrouching = false;
    }

    // Методи прийому команд

    public void commandMoveRight() {
        wantToMoveRight = true;
        if(onGround) {
            facingRight = true;
            go();
        }
    }

    public void commandStopMoveRight() {
        wantToMoveRight = false;
        if (onGround) {
            if (wantToMoveLeft) {
                facingRight = false;
                go();
            } else stop();

        }
    }

    public void commandMoveLeft() {
        wantToMoveLeft = true;
        if(onGround) {
            facingRight = false;
            go();
        }
    }

    public void commandStopMoveLeft() {
        wantToMoveLeft = false;
        if (onGround) {
            if (wantToMoveRight) {
                facingRight = true;
                go();
            } else stop();

        }
    }

    public void commandCrouch() {
        wantToCrouch = true;
        if (onGround) crouch();
    }

    public void commandStandUp() {
        wantToCrouch = false;
        if (isCrouching && canStand()) standUp();
    }

    public void commandJump(){
        if (isCrouching && canStand()) standUp();

        if (onGround && !isCrouching) jump();

    }

    public void commandStartShooting() {
        // дописати
    }

    public void commandStopShooting() {
        // дописати
    }

    public void commandEquipPistol() {
        currentWeapon = 0;
    }

    public void commandEquipAR (){
        if (weaponNumber > 0) currentWeapon = 1;
    }


    @Override
    protected void onLand(){
        if (wantToMoveRight ^ wantToMoveLeft){
            facingRight = wantToMoveRight;
            go();
        }
        else stop();

        if (wantToCrouch) commandCrouch();
        else commandStandUp();
    }



    private boolean canStand() {
        int heightDiff = defaultHeight - heightInCrouch;
        int testY = this.y - heightDiff;

        GameEntity object = collision(this.x, testY, this.width, heightDiff, Level.getCurrentLevel().getBlokingObjects());

        return object == null || object.isWalkable;
    }
}
