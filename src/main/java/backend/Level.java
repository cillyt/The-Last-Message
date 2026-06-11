package backend;

import backend.triggeredZones.Detector;
import backend.weapon.Bullet;
import backend.weapon.Weapon;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Level {

    private int x;
    private int y;
    private int width;
    private int height;
    private int levelNumber;

    // Стан рівня
    private boolean isFinished = false;

    @Getter
    private static Level currentLevel;

    private List<GameEntity> allObjects;
    private List<GameEntity> blokingObjects; // об'єкти з !isWalkable
    private List<GameEntity> playerDetectors; // детектори, які перевіряє гравець
    private List<GameEntity> independDetectors; // детектори, які самі себе перевіряють
    private List<GameEntity> bullets; // кулі всіх видів зброї
    private List<BlockOfGround> blocksOfGround;
    private List<SoundPrint> soundPrints = new ArrayList<>();
    private List<GameEntity> wallsAndPartBlocks;

    List<List<? extends GameEntity>> lists = new ArrayList<>(); // список списків

    public Level(int x, int y, int width, int height, int levelNumber, List<GameEntity> allObjects) {
        currentLevel = this;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.levelNumber = levelNumber;

        this.allObjects = allObjects;
        lists.add(allObjects);

        this.allObjects.sort(Comparator.comparingInt(GameEntity::getZIndex));

        initialLists();
    }

    // Методи завершення рівня
    public void win() {
        if (isFinished) return;
        isFinished = true;
        // логіка виграшу рівня
    }

    public void lose() {
        if (isFinished) return;
        isFinished = true;
        // логіка програшу
    }

    private void initialLists(){
        blokingObjects = allObjects.stream()
                .filter(obj -> !obj.isWalkable())
                .collect(Collectors.toList());
        lists.add(blokingObjects);

        blocksOfGround = blokingObjects.stream()
                .filter(obj -> obj instanceof BlockOfGround)
                .map(obj -> (BlockOfGround) obj)
                .collect(Collectors.toList());
        lists.add(blocksOfGround);

        wallsAndPartBlocks = blokingObjects.stream()
                .filter(obj -> obj instanceof BlockOfGround || obj instanceof PartialBlock)
                .collect(Collectors.toList());
        lists.add(wallsAndPartBlocks);

        playerDetectors = allObjects.stream()
                .filter(obj -> {
                    if(obj instanceof Detector){
                        Detector det = (Detector) obj;
                        return det.getTargetPlayer() == Player.getInstance();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        lists.add(playerDetectors);

        independDetectors = allObjects.stream()
                .filter(obj -> {
                    if(obj instanceof Detector){
                        Detector det = (Detector) obj;
                        return det.getTargetObjects() != null;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        lists.add(independDetectors);

        bullets = new ArrayList<>();
        for (Weapon weapon : Player.getInstance().getWeapons()) {
            if (weapon != null) {
                for (Bullet bullet : weapon.getBullets()) {
                    allObjects.add(bullet);
                    bullets.add(bullet);
                }
            }
        }
        lists.add(bullets);
    }

    public void update(double deltaTime) {
        if (soundPrints.isEmpty()) return;
        for(SoundPrint sound : soundPrints){
            sound.intensity -= 0.1 * deltaTime;
        }
        soundPrints.removeIf(sound -> sound.intensity <= 0);
    }

}
