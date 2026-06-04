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

    @Getter
    private static Level currentLevel;

    private List<GameEntity> allObjects;
    private List<GameEntity> blokingObjects; // об'єкти з !isWalkable
    private List<GameEntity> playerDetectors; // детектори, які перевіряє гравець
    private List<GameEntity> independDetectors; // детектори, які самі себе перевіряють
    private List<GameEntity> bullets; // кулі всіх видів зброї

    public Level (List<GameEntity> allObjects){
        currentLevel = this;

        this.allObjects = allObjects;
        this.allObjects.sort(Comparator.comparingInt(GameEntity::getZIndex));

        initialLists();
    }

    private void initialLists(){
        blokingObjects = allObjects.stream()
                .filter(obj -> !obj.isWalkable())
                .collect(Collectors.toList());

        playerDetectors = allObjects.stream()
                .filter(obj -> {
                    if(obj instanceof Detector){
                        Detector det = (Detector) obj;
                        return det.getTargetPlayer() == Player.getInstance();
                    }
                    return false;
                })
                .collect(Collectors.toList());

        independDetectors = allObjects.stream()
                .filter(obj -> {
                    if(obj instanceof Detector){
                        Detector det = (Detector) obj;
                        return det.getTargetObjects() != null;
                    }
                    return false;
                })
                .collect(Collectors.toList());

        bullets = new ArrayList<>();

        for (Weapon weapon : Player.getInstance().getWeapons()) {
            if (weapon != null) {
                for (Bullet bullet : weapon.getBullets()) {
                    allObjects.add(bullet);
                    bullets.add(bullet);
                }
            }
        }
    }

}
