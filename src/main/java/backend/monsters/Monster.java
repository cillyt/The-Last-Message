package backend.monsters;

import backend.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Monster extends MovingGameEntity implements Raycaster {

    protected enum BehavioralState {
        PATROL, // патруляє
        SCANE, // оглядається на при кінці патрулювання
        ESPY, // стан для затримки перед переслідуванням
        CHASE, // йде за героєм
        INVESTIGATE, // пошук за звуком
        LOSE, // втратив героя з поля зору
        ATTACK, // б'є героя
        COMEBACK, // повертається до патрулювання
        DYING // помирає
    }
    protected BehavioralState behState = BehavioralState.PATROL;

    protected int patrolRadius; // радіус патрулювання навколо точки спавну

    // крайні точки патрулювання
    protected int rightPatrolPoint;
    protected int leftPatrolPoint;

    protected int maxDistance = 2000; // максимальна дистанція відходу від точки спавна

    protected double currentTime;

    protected double scaneTime = 0.5; // час одного озирання
    protected double loseTime = 5; // час, який монстр буде стояти на місці при втраті гравця
    protected double dyingTime = 0.5; // час для анімації смероі

    protected double cooldown = 0.5; // час перед атакою

    protected boolean jumpOverHoles; // чи вміє перестрибувати ями

    protected double delayBefAgro = 1; // затримка, після якої монстр починає переслідувати гравця

    // Останні побачені координати гравця
    protected int lastSeenPlayerX;
    protected int lastSeenPlayerY;

    protected int hearingPower = 1000; // дистанція, на яку монстр чує звук з intencity = 1.0

    protected int damage;

    // зона ураження
    protected int attackWidth = 75;

    // зона бачення
    protected int frontVisionDistance = 1000;

    protected int eyeH; // відстань від верхівки лоба до очей

    protected int maxHp = 100;
    protected int currentHP;

    public Monster(int x, int y, int patrolRadius) {
        super(x, y);

        isWalkable = false;
        zIndex = 5;

        this.patrolRadius = patrolRadius;
        leftPatrolPoint = x - patrolRadius;
        rightPatrolPoint = x + patrolRadius;

    }

    // Методи переходів

    protected void toPatrol() {
        behState = BehavioralState.PATROL;
        currentVelocityX = facingRight ? speedX : -speedX;
        currentTime = 0;
        currentState = State.GO;
    }


    protected void toScane() {
        currentVelocityX = 0;
        currentState = State.STAND;
        currentTime = 0;

        behState = BehavioralState.SCANE;
    }

    protected void toEspy(){
        currentVelocityX = 0;
        currentState = State.STAND;
        facingRight = lastSeenPlayerX > x;
        currentTime = 0;

        behState = BehavioralState.ESPY;
    }

    protected void toChase() {
        currentTime = 0;
        moveToPoint(lastSeenPlayerX);

        behState = BehavioralState.CHASE;
    }

    protected void toInvestigate() {
        moveToPoint(lastSeenPlayerX);
    }

    protected void toLose() {
        currentTime = 0;
        currentVelocityX = 0;
        currentState = State.STAND;

        behState = BehavioralState.LOSE;
    }

    protected void toComeback() {
        moveToPoint(rightPatrolPoint);

        behState = BehavioralState.COMEBACK;
    }

    protected void toAttack() {
        currentVelocityX = 0;
        currentState = State.STAND;
        currentTime = 0;

        behState = BehavioralState.ATTACK;
    }

    protected void toDying() {
        currentVelocityX = 0;
        currentState = State.STAND;
        currentTime = 0;

        behState = BehavioralState.DYING;
    }

    // Допоміжні методи
    protected void moveToPoint(int targetX) {
        if(targetX > x){
            facingRight = true;
            currentVelocityX = speedX;
        }
        else {
            facingRight = false;
            currentVelocityX = -speedX;
        }
        currentState = State.GO;
    }

    protected boolean checkAttack(){
        int attackX = facingRight ? x + width : x - attackWidth;

        return collision(attackX, y, attackWidth, height, Player.getInstance());
    }

    // Методи помічання гравця
    protected boolean seePlayer() {
        Player player = Player.getInstance();
        boolean checkPlayerX = facingRight ?
                x - width/2 < player.getX() && player.getX() < x + width + frontVisionDistance :
                x - frontVisionDistance < player.getX() && player.getX() < x + width/2;

        if (checkPlayerX){

            // перевірка стін (для низа і верха гравця
            int eyeY = y + eyeH;
            int eyeX = facingRight ? x + width : x;

            int playerX = facingRight ? player.getX() : player.getX() + player.getWidth();
            int playerTopY = player.getY() + 5;
            int playerBottomY = player.getY() + player.getHeight() - 5;

            boolean seenTop = !isObjectOnTheLine(eyeX, eyeY, playerX, playerTopY, Level.getCurrentLevel().getBlocksOfGround());
            boolean seenBottom = !isObjectOnTheLine(eyeX, eyeY, playerX, playerBottomY, Level.getCurrentLevel().getBlocksOfGround());

            if(seenTop || seenBottom) {
                lastSeenPlayerX = player.getX();
                lastSeenPlayerY = player.getY();

                return true;
            }
        }

        return false;
    }

    protected boolean hearPlayer() {
        List<SoundPrint> heardSounds = new ArrayList<>();

        for (SoundPrint sound : Level.getCurrentLevel().getSoundPrints()){
            double distance = Math.max(calcDistance(sound.x, sound.y), 1.0);
            if (distance < hearingPower * sound.intensity) heardSounds.add(sound);
        }

        if (heardSounds.isEmpty()) return false;

        SoundPrint loudestSound = heardSounds.getFirst();
        double loudestSoundPower = loudestSound.intensity / Math.max(calcDistance(loudestSound.x, loudestSound.y), 1.0);
        for (SoundPrint sound : heardSounds) {
            double soundPower = sound.intensity / Math.max(calcDistance(sound.x, sound.y), 1.0);
            if(soundPower > loudestSoundPower) loudestSound = sound;
        }

        lastSeenPlayerX = loudestSound.x;
        lastSeenPlayerY = loudestSound.y;
        return true;
    }

    public void takeDamage(int damage) {
        currentHP -= damage;
        if (currentHP <= 0) toDying();
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        switch (behState) {
            case PATROL:
                if(seePlayer()) {
                    toEspy();
                    break;
                }
                else if(hearPlayer()){
                    toInvestigate();
                    break;
                }
                if(leftPatrolPoint > x || x > rightPatrolPoint) toScane();
                break;
            case SCANE:
                currentTime += deltaTime;
                if(seePlayer()) {
                    toEspy();
                    break;
                }
                // другий поворот
                if (currentTime >= 2 * scaneTime) {
                    facingRight = !facingRight;
                    toPatrol();
                }
                // перший поворот
                if (currentTime >= scaneTime) facingRight = !facingRight;
                break;
            case ESPY:
                currentTime += deltaTime;
                if (currentTime >= delayBefAgro){
                    toChase();
                }
                break;
            case CHASE:
                // оновлення координат гравця раз на 0.5 с
                currentTime += deltaTime;
                if(currentTime >= 0.5){
                    currentTime = 0;
                    seePlayer();
                    moveToPoint(lastSeenPlayerX);
                }
                // коли дійшов до останніх координат, які побачив
                double step = Math.abs(currentVelocityX) * deltaTime;
                if(x - step <= lastSeenPlayerX && lastSeenPlayerX <= x + step){
                    if (seePlayer()) toChase();
                    else if (hearPlayer()) toInvestigate();
                    else toLose();
                }
                // атака
                if(checkAttack()) toAttack();
                break;
            case LOSE:
                currentTime += deltaTime;
                if (currentTime > loseTime) toComeback();

                if (hearPlayer()) toInvestigate();
                if (seePlayer()) toChase();

                break;
            case COMEBACK:
                if (hearPlayer()) toInvestigate();
                if (seePlayer()) toChase();
                break;
            case ATTACK:
                currentTime += deltaTime;
                if(currentTime >= cooldown) {
                    currentTime = 0;
                    Player.getInstance().takeDamage(damage);
                }
                if(!checkAttack()) toChase();
                break;
            case DYING:
                currentTime += deltaTime;
                if(currentTime >= dyingTime) {
                    isActive = false;
                }
                break;
        }
    }
}
