package backend.monsters;

import backend.MovingGameEntity;
import backend.Player;
import lombok.Getter;

@Getter
public abstract class Monster extends MovingGameEntity {

    protected enum BehavioralState {
        PATROL, // патруляє
        SCANE, // оглядається на при кінці патрулювання
        ESPY, // стан для затримки перед переслідуванням
        CHASE, // йде за героєм
        INVESTIGATE,
        LOSE, // втратив героя з поля зору
        ATTACK, // б'є героя
        COMEBACK, // повертається до патрулювання
        DYING // помирає
    }
    protected BehavioralState behState;

    protected int patrolRadius; // радіус патрулювання навколо точки спавну

    // крайні точки патрулювання
    protected int rightPatrolPoint;
    protected int leftPatrolPoint;

    protected int maxDistance; // максимальна дистанція відходу від точки спавна

    protected double currentTime;
    protected double scaneTime; // час одного озирання

    protected boolean jumpOverHoles; // чи вміє перестрибувати ями

    protected double delayBefAgro; // затримка, після якої монстр починає переслідувати гравця

    // Останні побачені координати гравця
    protected int lastSeenPlayerX;
    protected int lastSeenPlayerY;

    protected int damage;
    protected int cooldown;

    // зона ураження
    protected int attackWidth;
    protected int attackHeight;

    // зони стеження
    protected int frontVisionDistance;
    protected int backVisionDistance;

    protected int hp;

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

        currentVelocityX = speedX;
        if (!facingRight) currentVelocityX *= -1;

        currentState = State.GO;
    }

    protected void toScane() {
        currentVelocityX = 0;
        currentState = State.STAND;

        behState = BehavioralState.SCANE;
    }

    protected void toEspy(){
        if(lastSeenPlayerX > x){
            facingRight = true;
        }
        else {
            facingRight = false;
        }
    }

    protected void toChase() {
        if(lastSeenPlayerX > x){
            facingRight = true;
            currentVelocityX = speedX;
        }
        else {
            facingRight = false;
            currentVelocityX = -speedX;
        }
        currentState = State.GO;
    }

    protected void toInvestigate() {

    }

    protected boolean detectPlayer() {
        int playerX = Player.getInstance().getX();
        if(facingRight) {
            if(x - backVisionDistance < playerX && playerX < x + frontVisionDistance) {

                lastSeenPlayerX = playerX;
                lastSeenPlayerY = Player.getInstance().getY();

                return true;
            }
        }
        else {
            if(x - frontVisionDistance < playerX && playerX < x + backVisionDistance){

                lastSeenPlayerX = playerX;
                lastSeenPlayerY = Player.getInstance().getY();

                return true;
            }
        }
        return false;
    }

    public void takeDamage(int damage) {
        hp -= damage;
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        switch (behState) {
            case PATROL:
                if(detectPlayer()) {
                    behState = BehavioralState.ESPY;
                    break;
                }

                if(leftPatrolPoint > x || x > rightPatrolPoint) toScane();
                break;
            case SCANE:
                currentTime += deltaTime;
                if(detectPlayer()) {
                    behState = BehavioralState.ESPY;
                    break;
                }
                // другий поворот
                if (currentTime >= 2 * scaneTime) {
                    facingRight = !facingRight;
                    currentTime = 0;
                    toPatrol();
                }
                // перший поворот
                if (currentTime >= scaneTime) facingRight = !facingRight;
                break;
            case ESPY:
                currentTime += deltaTime;
                if (currentTime >= delayBefAgro){
                    currentTime = 0;
                    toChase();
                }
                break;
            case CHASE:
                // оновлення координат гравця раз на 0.5 с
                currentTime += deltaTime;
                if(currentTime >= 0.5){
                    detectPlayer();
                    toChase();
                }
                // коли дійшов до останніх координат, які побачив
                double step = Math.abs(currentVelocityX) * deltaTime;
                if(x - step <= lastSeenPlayerX && lastSeenPlayerX <= x + step){
                    if (detectPlayer()) toChase();
                    else toInvestigate();
                }
        }
    }
}
