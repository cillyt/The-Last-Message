package backend;

import backend.triggeredZones.Detector;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class Level {

    @Getter
    private static Level currentLevel;

    private List<GameEntity> allObjects;
    private List<GameEntity> blokingObjects; // об'єкти з !isWalkable
    private List<Detector> playerDetectors; // детектори, які перевіряє гравець
    private List<Detector> independDetector; // детектори, які самі себе перевіряють

    public Level (List<GameEntity> allObjects){
        this.allObjects = allObjects;
        currentLevel = this;
        initialLists();
    }

    private void initialLists(){
        blokingObjects = allObjects.stream().filter(obj -> !obj.isWalkable).toList();
        // дописати
    }

}
