package backend.monsters;

import backend.*;
import javafx.scene.image.Image;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Monster extends MovingGameEntity implements Raycaster {

    protected enum BehavioralState {
        PATROL, // патрулює
        SCANE, // оглядається на при кінці патрулювання
        ESPY, // стан для затримки перед переслідуванням
        CHASE, // йде за героєм
        INVESTIGATE, // пошук за звуком
        LOSE, // втратив героя з поля зору
        STUCK, // бачить героя, але не може до нього дістатися
        RETREAT, // втеча після STUCK
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
    protected double dyingTime = 0.5; // час для анімації смерті
    protected double stuckAttackTimer;
    protected double stuckRoarTimer;

    protected double cooldown = 0.5; // час перед атакою

    protected boolean jumpOverHoles; // чи вміє перестрибувати ями

    protected double delayBefAgro = 0.5; // затримка, після якої монстр починає переслідувати гравця

    // Останні побачені координати гравця
    protected int lastSeenPlayerX;

    protected int hearingPower = 1000; // дистанція, на яку монстр чує звук з intencity = 1.0

    protected int damage = 10;

    // зона ураження
    protected int attackWidth = 75;

    // зона бачення
    protected int frontVisionDistance = 600;

    protected int eyeH; // відстань від верхівки лоба до очей

    protected int maxHp = 100;
    protected int currentHP;

    // ----- АСЕТИ -----
    protected Image standImg;
    protected Image jumpImg;

    protected Image [] moveImgs = new Image[6];
    protected Image [] attackImgs = new Image[4];
    protected Image [] dyingImgs = new Image[4];
    // -----------------

    protected double currentSpriteTime;
    protected int currentSpriteIndex;

    protected final double moveAnimTime = 1; // час прокручення всього масиву спрайтів
    protected double moveAnimPeriod; // час перемикання спрайтів

    protected double attackAnimTime;
    protected double attackAnimPeriod;

    protected final double dyingAnimTime = 1;
    protected double dyingAnimPeriod;

    // звуки
    protected SoundManager.SoundType agroSound;
    protected SoundManager.SoundType deathSound;

    protected boolean sawFirst = true;

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

    // викликати в нащадках
    protected void initialTimePeriods(){
        attackAnimTime = cooldown;
        moveAnimPeriod = moveAnimTime / moveImgs.length;
        attackAnimPeriod = attackAnimTime / attackImgs.length;
        dyingAnimPeriod = dyingAnimTime / dyingImgs.length;
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

        currentImage = standImg;
        currentSpriteTime = 0;
        currentSpriteIndex = 0;

    }

    protected void toEspy(){
        currentVelocityX = 0;
        currentState = State.STAND;
        facingRight = lastSeenPlayerX > x;
        currentTime = 0;

        behState = BehavioralState.ESPY;

        currentImage = standImg;
        currentSpriteTime = 0;
        currentSpriteIndex = 0;
    }

    protected void toChase() {
        currentTime = 0;
        moveToPoint(lastSeenPlayerX);

        behState = BehavioralState.CHASE;

        if(sawFirst) {
            SoundManager.getInstance().play(agroSound);
            sawFirst = false;
        }
    }

    protected void toInvestigate() {
        moveToPoint(lastSeenPlayerX);
        sawFirst = false;
    }

    protected void toLose() {
        currentTime = 0;
        currentVelocityX = 0;
        currentState = State.STAND;

        behState = BehavioralState.LOSE;

        currentImage = standImg;
        currentSpriteTime = 0;
        currentSpriteIndex = 0;

        sawFirst = true;
    }

    protected void toStuck() {
        currentVelocityX = 0;
        currentState = State.STAND;
        currentTime = 0;
        stuckAttackTimer = 0;
        stuckRoarTimer = 0;

        behState = BehavioralState.STUCK;

        currentImage = standImg;
        currentSpriteTime = 0;
        currentSpriteIndex = 0;
    }

    protected void toRetreat() {
        facingRight = x > Player.getInstance().getX();
        currentVelocityX = facingRight ? speedX : -speedX;
        currentState = State.GO;

        behState = BehavioralState.RETREAT;

        SoundManager.getInstance().stop(agroSound);
        SoundManager.getInstance().play(agroSound);

        currentSpriteIndex = 1;
        currentImage = moveImgs[0];
        currentTime = 0;
    }

    protected void toComeback() {
        moveToPoint(rightPatrolPoint);

        behState = BehavioralState.COMEBACK;

        sawFirst = true;
    }

    protected void toAttack() {
        currentVelocityX = 0;
        currentState = State.STAND;
        currentTime = 0;

        behState = BehavioralState.ATTACK;

        currentSpriteTime = 0;
        currentSpriteIndex = 1;
        currentImage = attackImgs[0];
    }

    protected void toDying() {
        currentVelocityX = 0;
        currentState = State.STAND;
        currentTime = 0;

        behState = BehavioralState.DYING;

        currentSpriteTime = 0;
        currentSpriteIndex = 0;
        currentImage = dyingImgs[0];

        SoundManager.getInstance().play(deathSound);
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

        if(currentState != State.GO){
            currentSpriteIndex = 1;
            currentImage = moveImgs[0];
        }

        currentState = State.GO;
    }

    protected PathCondition checkObstacle() {
        int colWidth = maxJumpDistance / 2;
        int finalX = facingRight ? x + width : x - colWidth;
        int moveDir = facingRight ? 1 : -1;

        GameEntity obstacle = collision(finalX, y-1, colWidth, height, moveDir, 0, Level.getCurrentLevel().getWallsAndPartBlocks());

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

        // перевіряємо послідовно 5 положень монстра в стрибку
        int finalY = y - targetJumpHeight;

        int deltaX = (finalX - x) / 6;
        int deltaY = (finalY - y) / 6;

        boolean canJumpOver = true;

        for(int i = 0; i < 6; i++){
            finalX -= deltaX;
            finalY -= deltaY;

            canJumpOver = collision(finalX, finalY, width, height,
                    moveDir, 0, Level.getCurrentLevel().getWallsAndPartBlocks()) == null;

            if(!canJumpOver) break;
        }

        return canJumpOver ? PathCondition.PASSABLE : PathCondition.IMPASSABLE;
    }

    protected PathCondition checkHole() {
        // 1. Перевіряємл землю під ногами
        int checkX = facingRight ? x + width + 10 : x - 10;
        int checkY = y + height + 5;

        boolean hasGround = collision(checkX, checkY, 5, 50, 0, 1, Level.getCurrentLevel().getWallsAndPartBlocks()) != null;

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

                currentImage = jumpImg;
                currentSpriteTime = 0;
                currentSpriteIndex = 0;
            }
        }
    }

    // Реакція на глухий кут
    protected void handleImpassable() {
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
                toStuck();
                break;
            case RETREAT:
                this.rightPatrolPoint = this.x + patrolRadius;
                this.leftPatrolPoint = this.x - patrolRadius;
                toScane();
                break;
        }
    }

    protected boolean checkAttack(){
        int attackX = facingRight ? x + width : x - attackWidth;

        return collision(attackX, y, attackWidth/2, height, Player.getInstance());
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
        if (currentHP <= 0) {
            toDying();
        } else if (behState == BehavioralState.STUCK || behState == BehavioralState.LOSE) {
            toRetreat();
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);

        if(currentState == State.GO){
            currentSpriteTime += deltaTime;
            if(currentSpriteTime >= moveAnimPeriod){
                currentSpriteTime -= moveAnimPeriod;
                currentImage = moveImgs[currentSpriteIndex];
                currentSpriteIndex = (currentSpriteIndex + 1) % moveImgs.length;
            }
        }

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
                if (seePlayer()) {
                    toChase();
                    break;
                }

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
                stuckAttackTimer += deltaTime;
                stuckRoarTimer += deltaTime;

                // агро звук раз на 5 секунд
                if (stuckRoarTimer >= 5) {
                    stuckRoarTimer -= 5;
                    SoundManager.getInstance().stop(agroSound);
                    SoundManager.getInstance().play(agroSound);
                }

                // якщо минуло 5 секунд - здається і йде
                if (currentTime >= 7) {
                    toComeback();
                    break;
                }

                // атака в повітря раз на 3 секунди
                if (stuckAttackTimer >= 3.0) {
                    stuckAttackTimer -= 3.0;
                    currentSpriteTime = 0;
                    currentSpriteIndex = 0;
                    currentImage = attackImgs[0];
                }

                boolean isAttacking = false;
                for (Image img : attackImgs) {
                    if (currentImage == img) {
                        isAttacking = true;
                        break;
                    }
                }

                if (isAttacking) {
                    currentSpriteTime += deltaTime;
                    if(currentSpriteTime >= attackAnimPeriod){
                        currentSpriteTime -= attackAnimPeriod;
                        currentSpriteIndex++;
                        if (currentSpriteIndex < attackImgs.length) {
                            currentImage = attackImgs[currentSpriteIndex];
                        } else {
                            currentImage = standImg;
                            currentSpriteIndex = 0;
                        }
                    }
                }

                if (!seePlayer()) {
                    toLose();
                } else {
                    facingRight = lastSeenPlayerX > x;
                    if (checkObstacle() != PathCondition.IMPASSABLE && checkHole() != PathCondition.IMPASSABLE) {
                        toChase();
                    }
                }
                break;
            case RETREAT:
                currentTime += deltaTime;
                checkPathAndMove();

                if (currentTime >= 3.0) {
                    this.rightPatrolPoint = this.x + patrolRadius;
                    this.leftPatrolPoint = this.x - patrolRadius;
                    toScane();
                }
                break;
            case COMEBACK:
                checkPathAndMove();

                double cbStep = Math.abs(currentVelocityX) * deltaTime;
                if (x - cbStep <= rightPatrolPoint && rightPatrolPoint <= x + cbStep) {
                    toPatrol();
                }

                if (hearPlayer()) toInvestigate();
                if (seePlayer()) toChase();
                break;
            case ATTACK:
                currentTime += deltaTime;
                if (currentTime >= cooldown) {
                    currentTime = 0;

                    if (checkAttack()) {
                        Player.getInstance().takeDamage(damage);
                    } else {
                        toChase();
                    }
                }

                currentSpriteTime += deltaTime;
                if(currentSpriteTime >= attackAnimPeriod){
                    currentSpriteTime -= attackAnimPeriod;
                    currentImage = attackImgs[currentSpriteIndex];
                    currentSpriteIndex = (currentSpriteIndex + 1) % attackImgs.length;
                }
                break;
            case DYING:
                currentTime += deltaTime;
                if (currentTime >= dyingTime) {
                    isActive = false;
                }

                currentSpriteTime += deltaTime;
                if(currentSpriteTime >= dyingAnimPeriod){
                    currentSpriteTime -= dyingAnimPeriod;
                    if (currentSpriteIndex < dyingImgs.length - 1) {
                        currentSpriteIndex++;
                        currentImage = dyingImgs[currentSpriteIndex];
                    }
                }
                break;
        }
    }
}
