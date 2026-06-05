package backend;

import backend.monsters.BigMonster;
import backend.monsters.SimpleMonster;
import backend.triggeredZones.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class LevelParser {

    public static Level loadLevel(File jsonFile) throws Exception {
        String content = new String(Files.readAllBytes(jsonFile.toPath()));
        JSONObject root = new JSONObject(content);
        JSONObject entities = root.getJSONObject("entities");

        List<GameEntity> allObjects = new ArrayList<>();
        allObjects.add(Player.getInstance());

        for (String entityType : entities.keySet()) {
            if (entityType.equals("Supplies")) continue;

            JSONArray items = entities.getJSONArray(entityType);

            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                int x = obj.getInt("x");
                int y = obj.getInt("y");

                int w = obj.has("width") ? obj.getInt("width") : 0;
                int h = obj.has("height") ? obj.getInt("height") : 0;

                switch (entityType) {
                    case "Player":
                        Player.getInstance().setX(x);
                        Player.getInstance().setY(y);
                        break;
                    case "BlockOfGround":
                        allObjects.add(new BlockOfGround(x, y, w, h));
                        break;
                    case "Cashe":
                        allObjects.add(new Cashe(x, y, w, h));
                        break;
                    case "LevelEndTrigger":
                        allObjects.add(new LevelEndTrigger(x, y, w, h));
                        break;
                    case "AR_Item":
                        allObjects.add(new AR_Item(x, y));
                        break;
                    case "Medkit":
                        allObjects.add(new Medkit(x, y));
                        break;
                    case "AR_Ammunition":
                        allObjects.add(new AR_Ammunition(x, y, 30));
                        break;
                    case "SimpleMonster":
                        allObjects.add(new SimpleMonster(x, y));
                        break;
                    case "Monster":
                        allObjects.add(new BigMonster(x, y));
                        break;
                    case "Trap1":
                        allObjects.add(new Trap1(x, y));
                        break;
                    case "Trap2":
                        allObjects.add(new Trap2(x, y));
                        break;
                }
            }
        }

        return new Level(allObjects);
    }
}