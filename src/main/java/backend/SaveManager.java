package backend;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.json";

    // зберігаємо поточний прогрес та стан гравця
    public static void saveGame() {
        try {
            JSONObject json = new JSONObject();
            json.put("maxLevelReached", GameProgress.maxLevelReached);

            Player p = Player.getInstance();
            if (p != null) {
                json.put("hp", p.getCurrentHp());
                json.put("pistolUnlocked", p.getWeaponUnlocked()[0]);
                json.put("arUnlocked", p.getWeaponUnlocked()[1]);

                // зберігаємо набої автомата (пістолет має безкінечні)
                json.put("arAmmo", p.getWeapons()[1].getAmmunitionNumber());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE))) {
                writer.write(json.toString(4));
            }
        } catch (Exception e) {
            System.err.println("Помилка при збереженні гри: " + e.getMessage());
        }
    }

    // завантажуємо дані
    public static void loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return; // Якщо файлу немає, залишаються дефолтні значення

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            JSONObject json = new JSONObject(content.toString());

            GameProgress.maxLevelReached = json.optInt("maxLevelReached", 1);

            Player p = Player.getInstance();
            if (p != null) {
                p.setCurrentHp(json.optInt("hp", p.getMaxHp()));
                p.getWeaponUnlocked()[0] = json.optBoolean("pistolUnlocked", true);
                p.getWeaponUnlocked()[1] = json.optBoolean("arUnlocked", false);

                int savedArAmmo = json.optInt("arAmmo", 15);
                // оновлюємо кількість набоїв
                int currentArAmmo = p.getWeapons()[1].getAmmunitionNumber();
                p.getWeapons()[1].addAmmunition(savedArAmmo - currentArAmmo);
            }
        } catch (Exception e) {
            System.err.println("Помилка при завантаженні гри: " + e.getMessage());
        }
    }
}