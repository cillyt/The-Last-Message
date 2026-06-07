/*
  Протагоніст
 */

package backend;

import backend.triggeredZones.Detector;
import backend.weapon.AR;
import backend.weapon.Pistol;
import backend.weapon.Weapon;
import lombok.Getter;


import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

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

    private Weapon[] weapons;
    private boolean[] weaponUnlocked;
    private Weapon currentWeapon;

    private double currentTime;
    private double soundPeriod = 5.0; // час між записами звуків при ходьбі


    public Player(int x, int y) {
        super(x, y);

        instance = this;

        height = 170;
        width = 50;

        isWalkable = false;
        zIndex = 4;

        speedX = 300;
        this.targetJumpHeight = 120;
        this.startJumpSpeed = -Math.sqrt(2 * gravity * this.targetJumpHeight);

        currentVelocityX = 0;
        currentVelocityY = 0;

        weapons = new Weapon[2];
        weaponUnlocked = new boolean[2];

        weapons[0] = new Pistol();
        weapons[1] = new AR();

        weaponUnlocked[0] = true;
        currentWeapon = weapons[0];

        // --- ЗАГЛУШКА ---
        Canvas canvas = new Canvas(width, height);
        GraphicsContext tempGc = canvas.getGraphicsContext2D();
        tempGc.setFill(Color.web("#00FFFF"));
        tempGc.fillRect(0, 0, width, height);
        tempGc.setFill(Color.BLACK);
        tempGc.setFont(new Font("Arial", 12));
        tempGc.fillText("Player", 5, 15);
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.image = canvas.snapshot(params, null);
        // ----------------
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

    private void jump() {
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
        currentWeapon.startFire();
    }

    public void commandStopShooting() {
        currentWeapon.stopFire();
    }

    public void commandEquipPistol() {
        if (weaponUnlocked[0]) {
            currentWeapon.stopFire();
            currentWeapon = weapons[0];
        }
    }

    public void commandEquipAR() {
        if (weaponUnlocked[1]) {
            currentWeapon.stopFire();
            currentWeapon = weapons[1];
        }
    }

    // Допоміжні методи руху

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

    // Методи детекторів

    public void healHp(int amount) {
        currentHp += amount;
        if (currentHp > maxHp) currentHp = maxHp;
    }

    public void unlockWeapon(int i) {
        weaponUnlocked[i] = true;
    }
    // -----------------

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        currentWeapon.update(deltaTime);

        GameEntity detector = collision(x, y, width, height, Level.getCurrentLevel().getPlayerDetectors());
        if (detector != null){
            Detector det = (Detector) detector;
            det.executeTrigger();
        }

        if(currentState == State.GO){
            currentTime += deltaTime;
            if(currentTime >= soundPeriod){
                currentTime -= soundPeriod;
                if(!isCrouching) Level.getCurrentLevel().getSoundPrints().add(new SoundPrint(x, y, 0.4));
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);

        // --- ЗАГЛУШКА ---
        backend.CameraWindow camera = backend.CameraWindow.getInstance();
        int screenX = this.x - camera.getX();
        int screenY = this.y - camera.getY();

        gc.setFill(Color.BLACK);
        gc.setFont(new javafx.scene.text.Font("Arial", 12));

        gc.fillText(String.format("Weapon \n %s \n %d \n\n HP\n %d",
                        currentWeapon, currentWeapon.getAmmunitionNumber(), currentHp)
                , screenX + 5, screenY + 35);
        // ----------------

    }
}
