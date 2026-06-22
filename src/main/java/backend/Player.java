/*
  Протагоніст
 */

package backend;

import backend.SoundManager.SoundType;
import backend.weapon.AR;
import backend.weapon.Pistol;
import backend.weapon.Weapon;
import javafx.scene.image.Image;
import lombok.Getter;


import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import lombok.Setter;

@Getter
@Setter
public class Player extends MovingGameEntity{

    @Getter
    private static Player instance;

    private final int defaultHeight = 160;
    private final int heightInCrouch = 120;

    private final int speedXinCrouch = 150;

    private boolean isCrouching = false;

    private final int maxHp = 100;
    private int currentHp = 100;

    private boolean wantToMoveRight = false;
    private boolean wantToMoveLeft = false;
    private boolean wantToCrouch = false;

    private final Weapon[] weapons;
    private final boolean[] weaponUnlocked;
    private Weapon currentWeapon;
    private int currentWeaponIndex;

    private double currentStepTime = 0.599;
    private final double soundPeriod = 0.6; // час між записами звуків при ходьбі

    private final int eyeH = 15;

    private boolean isDying = false;
    private boolean isDead = false;

    // ----- АСЕТИ -----

    // для пістолета
    private final Image standImgP = new Image(getAssetPath("assets/player/pistol/stand.png"));
    private final Image jumpImgP = new Image(getAssetPath("assets/player/pistol/назва.png"));
    private final Image crouchImgP = new Image(getAssetPath("assets/player/pistol/crouch.png"));

    private final Image[] moveImgsP = {
            new Image(getAssetPath("assets/player/pistol/move/prun1.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun2.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun3.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun4.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun5.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun6.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun7.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun8.png")),
            new Image(getAssetPath("assets/player/pistol/move/prun9.png"))
            // ...
    };
    private final Image[] crawlImgsP = { new Image(getAssetPath("assets/player/pistol/crawl/назва.png")),
            new Image(getAssetPath("assets/player/pistol/crawl/назва.png")),
            new Image(getAssetPath("assets/player/pistol/crawl/назва.png")),
            new Image(getAssetPath("assets/player/pistol/crawl/назва.png")),
            // ...
    };

    // для автомата
    private final Image standImgAR = new Image(getAssetPath("assets/player/ar/stand.png"));
    private final Image jumpImgAR = new Image(getAssetPath("assets/player/ar/jump.png"));
    private final Image crouchImgAR = new Image(getAssetPath("assets/player/ar/crouch.png"));

    private final Image[] moveImgsAR = {
            new Image(getAssetPath("assets/player/ar/move/arun0.png")),
            new Image(getAssetPath("assets/player/ar/move/arun1.png")),
            new Image(getAssetPath("assets/player/ar/move/arun2.png")),
            new Image(getAssetPath("assets/player/ar/move/arun3.png")),
            new Image(getAssetPath("assets/player/ar/move/arun4.png")),
            new Image(getAssetPath("assets/player/ar/move/arun5.png")),
            new Image(getAssetPath("assets/player/ar/move/arun6.png")),
            new Image(getAssetPath("assets/player/ar/move/arun7.png")),
            new Image(getAssetPath("assets/player/ar/move/arun8.png"))
    };
    private final Image[] crawlImgsAR = { new Image(getAssetPath("assets/player/ar/crawl/назва.png")),
            new Image(getAssetPath("assets/player/ar/crawl/назва.png")),
            new Image(getAssetPath("assets/player/ar/crawl/назва.png")),
            new Image(getAssetPath("assets/player/ar/crawl/назва.png")),
            // ...
    };

    // смерть
    private final Image[] dyingImgs = { new Image(getAssetPath("assets/player/dying/назва.png")),
            new Image(getAssetPath("assets/player/dying/назва.png")),
            new Image(getAssetPath("assets/player/dying/назва.png")),
            new Image(getAssetPath("assets/player/dying/назва.png")),
            // ...
    };

    // -----------------

    private double currentSpriteTime;

    private final double timeAnimationMove = 0.6; // час прокручення всього масиву спрайтів
    private final double periodAnimationMove; // час перемикання спрайтів

    private final double timeAnimationCrawl = 1;
    private final double periodAnimationCrawl;

    private final double timeAnimationDying = 1.5;
    private final double periodAnimationDying;

    private int currentSpriteIndex; // для бігу і повзання

    // масив звуків кроків
    private SoundManager.SoundType stepsSound = SoundType.footsteps;


    public Player(int x, int y) {
        super(x, y);

        instance = this;

        height = 170;
        width = 70;

        topImgMarg = 6;
        sideImgMarg = 63;

        isWalkable = false;
        zIndex = 4;

        speedX = 300;
        this.targetJumpHeight = 150;
        this.startJumpSpeed = -Math.sqrt(2 * gravity * this.targetJumpHeight);

        currentVelocityX = 0;
        currentVelocityY = 0;

        weapons = new Weapon[2];
        weaponUnlocked = new boolean[2];

        weapons[0] = new Pistol();
        weapons[1] = new AR();

        weaponUnlocked[0] = true;
        currentWeapon = weapons[0];

        periodAnimationMove = timeAnimationMove / moveImgsP.length;
        periodAnimationCrawl = timeAnimationCrawl / crawlImgsP.length;
        periodAnimationDying = timeAnimationDying / dyingImgs.length;

        calcImgMarg(0.57, standImgP);

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

    /**
     * Скидає стан гравця для початку рівня (здоров'я, позиція), але не чіпає інвентар.
     */
    public void reset() {
        currentHp = maxHp;
        isDying = false;
        isDead = false;
        wantToMoveRight = false;
        wantToMoveLeft = false;
        wantToCrouch = false;
        setSpeedModifier(1.0);
        commandEquipPistol();

        if (isCrouching) {
            y -= defaultHeight - heightInCrouch;
            topImgMarg -= defaultHeight - heightInCrouch;
        }
        isCrouching = false;
        height = defaultHeight;

        stop();
    }

    /**
     * Повністю скидає гравця до початкового стану, як на початку нової гри.
     */
    public void fullReset() {
        reset(); // Викликаємо звичайний reset

        // Додатково скидаємо зброю та патрони
        weaponUnlocked[0] = true; // Пістолет завжди доступний
        weaponUnlocked[1] = false; // Блокуємо автомат
        currentWeapon = weapons[0];
        currentWeaponIndex = 0;
        weapons[1].setAmmunitionNumber(0);
        currentImage = standImgP;
    }


    // Методи переходу в різні стани

    private void stop() {

        currentVelocityX = 0;
        currentState = State.STAND;

        if(isCrouching){
            if(currentWeaponIndex == 0) currentImage = crouchImgP;
            if(currentWeaponIndex == 1) currentImage = crouchImgAR;

        }
        else {
            if(currentWeaponIndex == 0) currentImage = standImgP;
            if(currentWeaponIndex == 1) currentImage = standImgAR;
        }

        currentSpriteTime = 0;
        currentSpriteIndex = 0;

        currentStepTime = 0.599;
        SoundManager.getInstance().stop(stepsSound);
    }

    private void go() {
        if (isCrouching) currentVelocityX = speedXinCrouch;
        else currentVelocityX = speedX;

        currentVelocityX *= speedModifier;

        if (!facingRight) currentVelocityX *= -1;

        if (currentState != State.GO) {
            currentState = State.GO;
            currentSpriteTime = 0;
            currentSpriteIndex = 0;
            if(!isCrouching) SoundManager.getInstance().playLoop(stepsSound);
        }
    }

    private void jump() {
        currentVelocityY = startJumpSpeed;
        currentState = State.IN_AIR;
        onGround = false;

        if (currentWeaponIndex == 0) currentImage = jumpImgP;
        if (currentWeaponIndex == 1) currentImage = jumpImgAR;

        currentSpriteTime = 0;
        currentSpriteIndex = 0;

        currentStepTime = 0.599;
        SoundManager.getInstance().stop(stepsSound);
    }

    private void crouch (){
        if (isCrouching) return;
        height = heightInCrouch;
        isCrouching = true;
        y += defaultHeight - heightInCrouch;

        topImgMarg += defaultHeight - heightInCrouch;

        if (currentState == State.GO){
            currentVelocityX = facingRight ? speedXinCrouch : -speedXinCrouch;
        }

        if (currentWeaponIndex == 0) currentImage = crouchImgP;
        if (currentWeaponIndex == 1) currentImage = crouchImgAR;

        currentSpriteTime = 0;
        currentSpriteIndex = 0;

        currentStepTime = 0.599;
        SoundManager.getInstance().stop(stepsSound);
    }

    private void standUp (){
        if(!isCrouching) return;
        height = defaultHeight;
        y -= defaultHeight - heightInCrouch;
        isCrouching = false;

        topImgMarg -= defaultHeight - heightInCrouch;

        if(currentState == State.GO){
            currentVelocityX = facingRight ? speedX : -speedX;
            SoundManager.getInstance().playLoop(stepsSound);
        }

        if (currentWeaponIndex == 0) currentImage = standImgP;
        if (currentWeaponIndex == 1) currentImage = standImgAR;

        currentSpriteTime = 0;
        currentSpriteIndex = 0;

        currentStepTime = 0.599;
    }

    // Методи прийому команд

    public void commandMoveRight() {
        if (wantToMoveRight) return;
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
        if (wantToMoveLeft) return;
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
        if(wantToCrouch) return;
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
        if (!weaponUnlocked[0] || currentWeaponIndex == 0) return;

        currentWeapon.stopFire();
        currentWeapon = weapons[0];
        currentWeaponIndex = 0;

        changeWeaponSprite();
        SoundManager.getInstance().playOnce(SoundType.gunChange);
    }

    public void commandEquipAR() {
        if (!weaponUnlocked[1] || currentWeaponIndex == 1) return;
        currentWeapon.stopFire();
        currentWeapon = weapons[1];
        currentWeaponIndex = 1;

        changeWeaponSprite();
        SoundManager.getInstance().playOnce(SoundType.gunChange);
    }

    // Допоміжні методи

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

        GameEntity object = collision(this.x, testY, this.width, heightDiff, 0, -1, Level.getCurrentLevel().getBlokingObjects());

        return object == null || object.isWalkable;
    }

    private void changeWeaponSprite() {
        if (currentState == State.IN_AIR) {
            switch (currentWeaponIndex) {
                case 0:
                    currentImage = jumpImgP;
                    break;
                case 1:
                    currentImage = jumpImgAR;
                    break;
                default:
                    currentImage = jumpImgP;
                    break;
            }
        } else if (currentState == State.STAND) {
            switch (currentWeaponIndex) {
                case 0:
                    currentImage = isCrouching ? crouchImgP : standImgP;
                    break;
                case 1:
                    currentImage = isCrouching ? crouchImgAR : standImgAR;
                    break;
                default:
                    currentImage = isCrouching ? crouchImgP : standImgP;
                    break;
            }
        } else if (currentState == State.GO) {
            Image[] activeArray = null;
            switch (currentWeaponIndex) {
                case 0:
                    activeArray = isCrouching ? crawlImgsP : moveImgsP;
                    break;
                case 1:
                    activeArray = isCrouching ? crawlImgsAR : moveImgsAR;
                    break;
            }
            if (currentSpriteIndex >= activeArray.length) currentSpriteIndex = 0;
            currentImage = activeArray[currentSpriteIndex];
        }
    }

    // Методи детекторів

    public void healHp(int amount) {
        currentHp += amount;
        if (currentHp > maxHp) currentHp = maxHp;
    }

    public void takeDamage(int damage){
        if (isDying || isDead) return;

        currentHp -= damage;
        CameraWindow.getInstance().applyShake(damage / 2, 0.2);

        if(currentHp > 0) return;

        stop();
        currentWeapon.stopFire();
        isDying = true;
        SoundManager.getInstance().play(SoundType.death1);
    }

    public void unlockWeapon(int i) {
        weaponUnlocked[i] = true;
    }
    // -----------------

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        currentWeapon.update(deltaTime);

        if (isDying && !isDead){
            currentSpriteTime += deltaTime;
            if(currentSpriteTime >= periodAnimationDying){
                currentSpriteTime -= periodAnimationDying;
                currentImage = dyingImgs[currentSpriteIndex];
                currentSpriteIndex++;
                if(currentSpriteIndex >= dyingImgs.length){
                    isDead = true;
                }
            }
        }

        if (isCrouching && !wantToCrouch && canStand()) {
            wantToCrouch = false;
            standUp();
        }

        // звуки
        if(currentState == State.GO){
            currentStepTime += deltaTime;
            if(currentStepTime >= soundPeriod){
                currentStepTime -= soundPeriod;
                if(!isCrouching){
                    Level.getCurrentLevel().getSoundPrints().add(new SoundPrint(x, y, 0.3));
                }
            }
        }

        // перемикання спрайтів
        if (currentState == State.GO) {
            currentSpriteTime += deltaTime;

            if (isCrouching) {
                if (currentSpriteTime >= periodAnimationCrawl) {
                    currentSpriteTime -= periodAnimationCrawl;
                    if (currentWeaponIndex == 0) {
                        currentImage = crawlImgsP[currentSpriteIndex];
                        currentSpriteIndex = (currentSpriteIndex + 1) % crawlImgsP.length;
                    } else if (currentWeaponIndex == 1) {
                        currentImage = crawlImgsAR[currentSpriteIndex];
                        currentSpriteIndex = (currentSpriteIndex + 1) % crawlImgsAR.length;
                    }
                }
            } else {
                if (currentSpriteTime >= periodAnimationMove) {
                    currentSpriteTime -= periodAnimationMove;
                    if (currentWeaponIndex == 0) {
                        currentImage = moveImgsP[currentSpriteIndex];
                        currentSpriteIndex = (currentSpriteIndex + 1) % moveImgsP.length;
                    } else if (currentWeaponIndex == 1) {
                        currentImage = moveImgsAR[currentSpriteIndex];
                        currentSpriteIndex = (currentSpriteIndex + 1) % moveImgsAR.length;
                    }
                }
            }
        }
    }

    @Override
    public void setSpeedModifier(double modifier) {
        this.speedModifier = modifier;
        if (currentState == State.GO) {
            currentVelocityX = isCrouching ? speedXinCrouch : speedX;
            currentVelocityX *= speedModifier;
            if (!facingRight) currentVelocityX *= -1;
        }
    }
}
