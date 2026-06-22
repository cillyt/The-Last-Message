/*
 * Author: П'ятаченко Гліб
 * File: SaveManager.java
 * Description: Менеджер порівневого збереження прогресу гри, ХП та стану зброї у JSON.
 */
package backend;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.json";
    private static JSONObject rootJson = new JSONObject();

    public static void saveStateForLevel(int levelNumber) {
        try {
            rootJson.put("maxLevelReached", GameProgress.maxLevelReached);

            Player p = Player.getInstance();
            JSONObject levelState = new JSONObject();
            if (p != null) {
                levelState.put("hp", p.getCurrentHp());
                levelState.put("arUnlocked", p.getWeaponUnlocked()[1]);
                levelState.put("arAmmo", p.getWeapons()[1].getAmmunitionNumber());
            }

            if (!rootJson.has("levelStates")) {
                rootJson.put("levelStates", new JSONObject());
            }
            rootJson.getJSONObject("levelStates").put(String.valueOf(levelNumber), levelState);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
                writer.write(rootJson.toString(4));
            }
        } catch (Exception e) {
            System.err.println("Помилка при збереженні гри: " + e.getMessage());
        }
    }

    public static void loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            resetProgress();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            rootJson = new JSONObject(content.toString());
            GameProgress.maxLevelReached = rootJson.optInt("maxLevelReached", 1);
        } catch (Exception e) {
            System.err.println("Помилка при завантаженні гри: " + e.getMessage());
            resetProgress();
        }
    }

    public static void loadStateForLevel(int levelNumber) {
        Player p = Player.getInstance();
        if (p == null) return;

        if (rootJson.has("levelStates") && rootJson.getJSONObject("levelStates").has(String.valueOf(levelNumber))) {
            JSONObject levelState = rootJson.getJSONObject("levelStates").getJSONObject(String.valueOf(levelNumber));
            p.setCurrentHp(levelState.optInt("hp", p.getMaxHp()));
            p.getWeaponUnlocked()[1] = levelState.optBoolean("arUnlocked", false);
            p.getWeapons()[1].setAmmunitionNumber(levelState.optInt("arAmmo", 0));
        } else if (levelNumber == 1) {
            p.setCurrentHp(p.getMaxHp());
            p.getWeaponUnlocked()[1] = false;
            p.getWeapons()[1].setAmmunitionNumber(0);
        }
    }

    public static void resetProgress() {
        GameProgress.maxLevelReached = 1;
        GameProgress.introCutscenePlayed = false;

        Player.getInstance().fullReset();
        rootJson = new JSONObject();

        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
        saveStateForLevel(1);
    }
}