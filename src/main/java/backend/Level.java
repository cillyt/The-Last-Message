package backend;

import backend.triggeredZones.Detector;
import backend.weapon.Bullet;
import backend.weapon.Weapon;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Getter
public class Level {

    @Getter
    private static Level currentLevel;

    private List<GameEntity> allObjects;
    private List<GameEntity> blokingObjects; // об'єкти з !isWalkable
    private List<Detector> playerDetectors; // детектори, які перевіряє гравець
    private List<Detector> independDetector; // детектори, які самі себе перевіряють
    private List<Detector> bullets; // кулі всіх видів зброї

    public Level (List<GameEntity> allObjects){
        currentLevel = this;

        this.allObjects = allObjects;
        this.allObjects.sort(Comparator.comparingInt(GameEntity::getZIndex));

        initialLists();
    }

    private void initialLists(){
        blokingObjects = allObjects.stream().filter(obj -> !obj.isWalkable).toList();

        for (Weapon weapon : Player.getInstance().getWeapons()) {
            if (weapon != null) {
                for (Bullet bullet : weapon.getBullets()) {
                    allObjects.add(bullet);
                }
            }
        }
        // дописати
    }

}
