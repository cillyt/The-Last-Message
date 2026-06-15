package backend;

import backend.monsters.BigMonster;
import backend.monsters.LeapingMonster;
import backend.monsters.SimpleMonster;
import backend.triggeredZones.*;
import backend.ui.StateManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class LevelParser {

    public static Level loadLevel(StateManager manager, File jsonFile) throws Exception {
        String content = new String(Files.readAllBytes(jsonFile.toPath()));
        JSONObject root = new JSONObject(content);

        int levelX = root.optInt("x", 0);
        int levelY = root.optInt("y", 0);
        int levelWidth = root.optInt("width", 2000);
        int levelHeight = root.optInt("height", 1000);

        String identifier = root.optString("identifier", "Level_0");
        int levelNumber = 0;
        try {
            // беремо цифру з назви (напр. "Level_0" -> 0)
            levelNumber = Integer.parseInt(identifier.replace("Level_", ""));
        } catch (NumberFormatException e) {
            System.err.println("Не вдалося розпізнати номер рівня з: " + identifier);
        }

        JSONObject entities = root.getJSONObject("entities");

        List<GameEntity> allObjects = new ArrayList<>();
        allObjects.add(Player.getInstance());

        for (String entityType : entities.keySet()) {
            if (entityType.equals("Doors")) continue;

            JSONArray items = entities.getJSONArray(entityType);

            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                int x = obj.getInt("x")+levelX;
                int y = obj.getInt("y")+levelY;

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
                        int ammo = 10;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("AmmunitionNumber")) {
                                ammo = custom.getInt("AmmunitionNumber");
                            }
                        }
                        allObjects.add(new AR_Ammunition(x, y, ammo));
                        break;
                    case "SimpleMonster":
                        int patrolRadius = 300;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("patrolRadius")) {
                                patrolRadius = custom.getInt("patrolRadius");
                            }
                        }
                        allObjects.add(new SimpleMonster(x, y, patrolRadius));
                        break;
                    case "BigMonster":
                        patrolRadius = 300;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("patrolRadius")) {
                                patrolRadius = custom.getInt("patrolRadius");
                            }
                        }
                        allObjects.add(new BigMonster(x, y, patrolRadius));
                        break;
                    case "LeapingMonster":
                        patrolRadius = 300;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("patrolRadius")) {
                                patrolRadius = custom.getInt("patrolRadius");
                            }
                        }
                        allObjects.add(new LeapingMonster(x, y, patrolRadius));
                        break;
                    case "Trap1":
                        allObjects.add(new Trap1(x, y));
                        break;
                    case "Trap2":
                        double fireTime = 8;
                        double breakTime = 5;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("fireTime")) {
                                fireTime = custom.getDouble("fireTime");
                            }
                            if (custom.has("breakTime")) {
                                breakTime = custom.getDouble("breakTime");
                            }
                        }
                        allObjects.add(new Trap2(x, y, fireTime, breakTime));
                        break;
                    case "PartialBlock":
                        PartialBlock.BlockDirection blockDir = PartialBlock.BlockDirection.TOP;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("blockDirection")) {
                                blockDir = PartialBlock.BlockDirection.valueOf(custom.getString("blockDirection").toUpperCase());
                            }
                        }
                        allObjects.add(new PartialBlock(x, y, w, h, blockDir));
                        break;
                    case "DeadZone":
                        allObjects.add(new DeadZone(x, y, w, h));
                    case "PopupMessageTrigger":
                        boolean triggerOnce = true;
                        String message = "";
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("triggerOnce")) {
                                triggerOnce = custom.optBoolean("triggerOnce", true);
                            }
                            if(custom.has("message")){
                                message = custom.getString("message");
                            }
                        }
                        allObjects.add(new PopupMessageTrigger(x, y, w, h, triggerOnce, message));
                }
            }
        }

        return new Level(manager, levelX, levelY, levelWidth, levelHeight, levelNumber, allObjects);
    }
}