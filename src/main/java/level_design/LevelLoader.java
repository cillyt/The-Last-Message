//package level_design;
//
//
//import com.google.gson.Gson;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//
//
//public class LevelLoader {
//
//    public static void loadLevel(String resourcePath) {
//        try {
//            Gson gson = new Gson();
//            InputStream inputStream = LevelLoader.class.getResourceAsStream(resourcePath);
//            if (inputStream == null) {
//                System.err.println("Файл " + resourcePath + " не знайдено");
//                return;
//            }
//
//            Reader reader = new InputStreamReader(inputStream);
//            LevelData levelData = gson.fromJson(reader, LevelData.class);
//            spawnEntities(levelData);
//
//        } catch (Exception e) {
//            System.err.println("Помилка завантаження рівня: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}
