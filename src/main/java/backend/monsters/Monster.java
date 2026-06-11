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
        STUCK, // бачить героя, але не може до нього дістатися
        ATTACK, // б'є героя
        COMEBACK, // повертається до патрулювання
        DYING // помирає
    }

    // Стан шляху попереду
    protected enum PathCondition {
        CLEAR,       // шлях чистий
        PASSABLE,    // є перешкода/яма, але можна перестрибнути
        IMPASSABLE   // нездоланна перешкода або заширока яма
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

    protected int hearingPower = 1000; // дистанція, на яку монстр чує звук з intencity = 1.0

    protected int damage = 10;

    // зона ураження
    protected int attackWidth = 75;

    // зона бачення
    protected int frontVisionDistance = 1000;

    protected int eyeH; // відстань від верхівки лоба до очей

    protected int maxHp = 100;
    protected int currentHP;

    public Monster(int x, int y) {
        super(x, y);

        isWalkable = false;
        zIndex = 5;

        patrolRadius = 500;
        leftPatrolPoint = x - patrolRadius;
        rightPatrolPoint = x + patrolRadius;

    }

    public Monster(int x, int y, int patrolRadius) {
        super(x, y);

        isWalkable = false;
        zIndex = 5;

        this.patrolRadius = patrolRadius;
        leftPatrolPoint = x - patrolRadius;
        rightPatrolPoint = x + patrolRadius;

    }

    @Override
    protected List<GameEntity> getCollisionObjects() {
        return Level.getCurrentLevel().getWallsAndPartBlocks();
    }

    // Методи переходів

    protected void toPatrol() {
        moveToPoint(rightPatrolPoint);
        behState = BehavioralState.PATROL;
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

    protected void toStuck() {
        currentVelocityX = 0;
        currentState = State.STAND;
        currentTime = 0;
        behState = BehavioralState.STUCK;
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

    protected PathCondition checkObstacle() {
        int colWidth = maxJumpDistance / 2;
        int colX = facingRight ? x + width : x - colWidth;
        int moveDir = facingRight ? 1 : -1;

        GameEntity obstacle = collision(colX, y-1, colWidth, height, moveDir, 0, Level.getCurrentLevel().getWallsAndPartBlocks());

        if (obstacle == null) return PathCondition.CLEAR;

        // перевірка, чи не притиснутий герой до стіни
        if (behState == BehavioralState.CHASE) {
            Player player = Player.getInstance();
            int pX = player.getX();

            if (facingRight && pX > this.x && pX < obstacle.getX()) {
                return PathCondition.CLEAR;
            }
            if (!facingRight && pX < this.x && pX + player.getWidth() > obstacle.getX() + obstacle.getWidth()) {
                return PathCondition.CLEAR;
            }
        }

        int apexY = y - targetJumpHeight;
        boolean canJumpOver = collision(colX, apexY, colWidth, height, moveDir, 0, Level.getCurrentLevel().getWallsAndPartBlocks()) == null;

        return canJumpOver ? PathCondition.PASSABLE : PathCondition.IMPASSABLE;
    }

    protected PathCondition checkHole() {
        // 1. Перевіряємл землю під ногами
        int checkX = facingRight ? x + width + 10 : x - 10;
        int checkY = y + height + 5;

        boolean hasGround = collision(checkX, checkY, 5, 5, 0, 1, Level.getCurrentLevel().getWallsAndPartBlocks()) != null;

        if (hasGround) return PathCondition.CLEAR;

        // стрибаємо в яму за гравцем, якщо треба
        if (behState == BehavioralState.CHASE) {
            Player player = Player.getInstance();
            boolean playerInHole = facingRight ? player.getX() >= this.x : player.getX() <= this.x;

            if (playerInHole && player.getY() > this.y) {
                return PathCondition.CLEAR;
            }
        }

        if (jumpOverHoles) {
            int landingX = facingRight ? x + maxJumpDistance : x - maxJumpDistance;
            boolean hasLanding = collision(landingX, checkY, 5, 5, 0, 1, Level.getCurrentLevel().getWallsAndPartBlocks()) != null;

            if (hasLanding) return PathCondition.PASSABLE;
        }

        // 3. Яма заширока
        return PathCondition.IMPASSABLE;
    }

    // Загальний метод оцінки шляху перед рухом
    protected void checkPathAndMove() {
        if (!onGround) return;

        PathCondition obstacle = checkObstacle();
        PathCondition hole = checkHole();

        if (obstacle == PathCondition.IMPASSABLE || hole == PathCondition.IMPASSABLE) {
            handleImpassable();
        } else if (obstacle == PathCondition.PASSABLE || hole == PathCondition.PASSABLE) {
            // стрибаємо
            if(onGround) {
                currentVelocityX = facingRight ? speedX : -speedX;
                currentVelocityY = startJumpSpeed;
                currentState = State.IN_AIR;
                onGround = false;
            }
        }
    }

    // Реакція на глухий кут
    private void handleImpassable() {
        currentVelocityX = 0;
        currentState = State.STAND;

        switch (behState) {
            case PATROL:
                // перезаписуємо межу патрулювання, щоб не битися в стіну знову
                if (facingRight) rightPatrolPoint = this.x;
                else leftPatrolPoint = this.x;
                toScane();
                break;
            case INVESTIGATE:
                toScane();
                break;
            case COMEBACK:
                // якщо шлях відрізано - новий дім
                this.rightPatrolPoint = this.x + patrolRadius;
                this.leftPatrolPoint = this.x - patrolRadius;
                toPatrol();
                break;
            case CHASE:
                toLose();
                break;
        }
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

        if (y - 500 >= player.getY() || player.getY() >= y + 200) return false;

        if (checkPlayerX){

            // перевірка стін (для низа і верха гравця)
            int eyeY = y + eyeH;
            int eyeX = facingRight ? x + width : x;

            int playerX = facingRight ? player.getX() : player.getX() + player.getWidth();
            int playerTopY = player.getY() + 5;
            int playerBottomY = player.getY() + player.getHeight() - 5;

            boolean seenTop = !isObjectOnTheLine(eyeX, eyeY, playerX, playerTopY, Level.getCurrentLevel().getBlocksOfGround());
            boolean seenBottom = !isObjectOnTheLine(eyeX, eyeY, playerX, playerBottomY, Level.getCurrentLevel().getBlocksOfGround());

            if(seenTop || seenBottom) {
                lastSeenPlayerX = player.getX();

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

        if (y - 500 >= loudestSound.y || loudestSound.y >= y + 200) return false;


        lastSeenPlayerX = loudestSound.x;
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
                if (seePlayer()) {
                    toEspy();
                    break;
                } else if (hearPlayer()) {
                    toInvestigate();
                    break;
                }

                checkPathAndMove();

                if (leftPatrolPoint > x || x > rightPatrolPoint) toScane();
                break;
            case SCANE:
                double prevTime = currentTime;
                currentTime += deltaTime;

                if (seePlayer()) {
                    toEspy();
                    break;
                }
                if (currentTime >= 2 * scaneTime) {
                    facingRight = !facingRight;
                    toPatrol();
                }
                else if (currentTime >= scaneTime && prevTime < scaneTime) {
                    facingRight = !facingRight;
                }
                break;
            case ESPY:
                currentTime += deltaTime;
                if (currentTime >= delayBefAgro) {
                    toChase();
                }
                break;
            case CHASE:
                currentTime += deltaTime;
                if (currentTime >= 0.2) {
                    currentTime = 0;
                    hearPlayer();
                    seePlayer();
                    moveToPoint(lastSeenPlayerX);
                }

                checkPathAndMove();

                double step = Math.abs(currentVelocityX) * deltaTime;
                if (x - step <= lastSeenPlayerX && lastSeenPlayerX <= x + step) {
                    if (seePlayer()) toChase();
                    else if (hearPlayer()) toInvestigate();
                    else toLose();
                }
                if (checkAttack()) toAttack();
                break;
            case INVESTIGATE:
                checkPathAndMove();

                double invStep = Math.abs(currentVelocityX) * deltaTime;
                if (x - invStep <= lastSeenPlayerX && lastSeenPlayerX <= x + invStep) {
                    toScane();
                }
                break;
            case LOSE:
                currentTime += deltaTime;
                if (currentTime > loseTime) toComeback();
                if (hearPlayer()) toInvestigate();
                if (seePlayer()) toChase();
                break;
            case STUCK:
                currentTime += deltaTime;
                if (currentTime >= 0.5) {
                    currentTime = 0;

                    if (!seePlayer()) {
                        toLose();
                    } else {
                        facingRight = lastSeenPlayerX > x;

                        if (checkObstacle() != PathCondition.IMPASSABLE && checkHole() != PathCondition.IMPASSABLE) {
                            toChase();
                        }
                    }
                }
                break;
            case COMEBACK:
                checkPathAndMove();

                if (hearPlayer()) toInvestigate();
                if (seePlayer()) toChase();
                break;
            case ATTACK:
                currentTime += deltaTime;
                if (currentTime >= cooldown) {
                    currentTime = 0;
                    Player.getInstance().takeDamage(damage);
                }
                if (!checkAttack()) toChase();
                break;
            case DYING:
                currentTime += deltaTime;
                if (currentTime >= dyingTime) {
                    isActive = false;
                }
                break;
        }
    }

    @Override
    public void render(javafx.scene.canvas.GraphicsContext gc) {
        super.render(gc);

        if (!isActive || !inCamera) return;

        backend.CameraWindow camera = backend.CameraWindow.getInstance();
        int screenX = this.x - camera.getX();
        int screenY = this.y - camera.getY();

        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(new javafx.scene.text.Font("Arial", 12));

        gc.fillText("behState:\n" + behState.name(), screenX + 5, screenY + 40);
    }
}