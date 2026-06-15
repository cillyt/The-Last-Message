package backend;

import backend.monsters.Monster;
import backend.triggeredZones.Detector;
import backend.ui.FinalCutsceneState;
import backend.ui.GameOverState;
import backend.ui.LevelCompleteState;
import backend.ui.StateManager;
import backend.weapon.Bullet;
import backend.weapon.Weapon;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Level {
    private final StateManager manager;

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
    private List<GameEntity> blokingObjects; // об'єкти з !isWalkable: гравець, монстри, блоки, часткові блоки
    private List<GameEntity> livingEntitties; // тільки гравець і монстри
    private List<GameEntity> wallsAndPartBlocks; // блоки землі і часткові блоки
    private List<BlockOfGround> blocksOfGround; // суто блоки землі

    private List<GameEntity> detectors; // детектори, які перевіряє гравець

    private List<GameEntity> bullets; // кулі всіх видів зброї

    private List<SoundPrint> soundPrints = new ArrayList<>(); // звуки

    List<List<? extends GameEntity>> lists = new ArrayList<>(); // список списків

    public Level(StateManager manager, int x, int y, int width, int height, int levelNumber, List<GameEntity> allObjects) {
        this.manager = manager;
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
        initialDetectors();
    }

    // Методи завершення рівня
    public void win() {
        if (isFinished) return;
        isFinished = true;

        if (levelNumber == 3) {
            manager.changeState(new FinalCutsceneState(manager));
        } else {
            manager.changeState(new LevelCompleteState(manager));
        }
    }

    public void lose() {
        if (isFinished) return;
        isFinished = true;
        manager.changeState(new GameOverState(manager));
    }

    private void initialLists(){
        blokingObjects = allObjects.stream()
                .filter(obj -> !obj.isWalkable())
                .collect(Collectors.toList());
        lists.add(blokingObjects);

        wallsAndPartBlocks = blokingObjects.stream()
                .filter(obj -> obj instanceof BlockOfGround || obj instanceof PartialBlock)
                .collect(Collectors.toList());
        lists.add(wallsAndPartBlocks);

        blocksOfGround = wallsAndPartBlocks.stream()
                .filter(obj -> obj instanceof BlockOfGround)
                .map(obj -> (BlockOfGround) obj)
                .collect(Collectors.toList());
        lists.add(blocksOfGround);

        livingEntitties = blokingObjects.stream()
                .filter(obj -> obj instanceof Player || obj instanceof Monster)
                .collect(Collectors.toList());
        lists.add(livingEntitties);


        detectors = allObjects.stream()
                .filter(obj -> {
                    if(obj instanceof Detector){
                        Detector det = (Detector) obj;
                        return det.getTargetPlayer() == Player.getInstance();
                    }
                    return false;
                })
                .collect(Collectors.toList());
        lists.add(detectors);

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

    private void initialDetectors() {
        for (GameEntity obj : detectors){
            Detector det = (Detector) obj;
            det.initialTargetList();
        }
    }

}
