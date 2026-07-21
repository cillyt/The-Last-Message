package backend;

import backend.background.BackgroundTexture;
import backend.monsters.BigMonster;
import backend.monsters.LeapingMonster;
import backend.monsters.SimpleMonster;
import backend.triggeredZones.*;
import backend.ui.StateManager;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LevelParser {

    private static int casheCounter = 0;

    public static Level loadLevel(StateManager manager, InputStream jsonStream) throws Exception {
        // Повністю очищуємо попередній рівень перед завантаженням нового
        Level.clearLevel();


        String content;
        try (Scanner scanner = new Scanner(jsonStream, StandardCharsets.UTF_8.name())) {
            content = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
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

        // текстури фону
        int width = 4000; // довжина одної текстури
        int numberFullBlocks = levelWidth / width; // скільки є цілих блоків довжиною width
        String basePath = "/assets/back_textures/level" + levelNumber + "/row-1-column-";

        for (int i = 1; i <= numberFullBlocks; i++){
            String fullPath = basePath + i + ".png";
            java.net.URL imageUrl = LevelParser.class.getResource(fullPath);
            if (imageUrl != null) {
                Image bacKTexture = new Image(imageUrl.toExternalForm());
                BackgroundTexture bTexture = new BackgroundTexture(levelX + width * (i-1), levelY, width, levelHeight, bacKTexture);
                allObjects.add(bTexture);
            }
        }

        // чи є неповний блок
        int remainderWidth = levelWidth % width;
        if (remainderWidth > 0) {
            String remainderPath = basePath + (numberFullBlocks+1) + ".png";
            java.net.URL imageUrl = LevelParser.class.getResource(remainderPath);
            if (imageUrl != null) {
                Image bacKTexture = new Image(imageUrl.toExternalForm());
                BackgroundTexture bTexture = new BackgroundTexture(levelX + width * numberFullBlocks, levelY, remainderWidth, levelHeight, bacKTexture);
                allObjects.add(bTexture);
            }
        }

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
                        Player.getInstance().setY(y-10);
                        break;
                    case "BlockOfGround":
                        allObjects.add(new BlockOfGround(x, y, w, h));
                        break;
                    case "Cashe":
                        if (levelNumber == 1) {
                            if (casheCounter < 1 || casheCounter >= 2) casheCounter = 1;
                            else casheCounter++;
                        }
                        else if (levelNumber == 2) {
                            if (casheCounter < 3 || casheCounter >= 4) casheCounter = 3;
                            else casheCounter++;
                        }
                        else if (levelNumber == 3) {
                            if (casheCounter < 5 || casheCounter >= 6) casheCounter = 5;
                            else casheCounter++;
                        }
                        String imgName = "cashe" + casheCounter;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("imageName")) {
                                imgName = custom.getString("imageName");
                            }
                        }
                        allObjects.add(new Cashe(x, y, w, h, imgName));
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
                        allObjects.add(new SimpleMonster(x, y-10, patrolRadius));
                        break;
                    case "BigMonster":
                        patrolRadius = 300;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("patrolRadius")) {
                                patrolRadius = custom.getInt("patrolRadius");
                            }
                        }
                        allObjects.add(new BigMonster(x, y-10, patrolRadius));
                        break;
                    case "LeapingMonster":
                        patrolRadius = 300;
                        if (obj.has("customFields")) {
                            JSONObject custom = obj.getJSONObject("customFields");
                            if (custom.has("patrolRadius")) {
                                patrolRadius = custom.getInt("patrolRadius");
                            }
                        }
                        allObjects.add(new LeapingMonster(x, y-10, patrolRadius));
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
                    case "Deadzone":
                        allObjects.add(new DeadZone(x, y, w, h));
                        break;
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
