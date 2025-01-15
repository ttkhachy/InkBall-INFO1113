package inkball;

import java.util.HashMap;
import java.util.Map;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Config {

    public static void loadLevelsFromConfig(App app, String configPath) {
        JSONObject json = app.loadJSONObject(configPath);
        JSONArray levelsArray = json.getJSONArray("levels");

        for (int i = 0; i < levelsArray.size(); i++) {
            JSONObject levelObj = levelsArray.getJSONObject(i);

            String layout = levelObj.getString("layout");
            int time = levelObj.getInt("time");
            int spawnInterval = levelObj.getInt("spawn_interval");
            float scoreIncreaseModifier = levelObj.getFloat("score_increase_from_hole_capture_modifier");
            float scoreDecreaseModifier = levelObj.getFloat("score_decrease_from_wrong_hole_modifier");
            String[] balls = levelObj.getJSONArray("balls").getStringArray();

            JSONObject scoreIncreaseJson = json.getJSONObject("score_increase_from_hole_capture");
            Map<String, Integer> scoreIncrease = new HashMap<>();

            for (Object keyObj : scoreIncreaseJson.keys()) {
                String key = (String) keyObj;
                scoreIncrease.put(key, scoreIncreaseJson.getInt(key));
            }

            JSONObject scoreDecreaseJson = json.getJSONObject("score_decrease_from_wrong_hole");
            Map<String, Integer> scoreDecrease = new HashMap<>();

            for (Object keyObj : scoreDecreaseJson.keys()) {
                String key = (String) keyObj;
                scoreDecrease.put(key, scoreDecreaseJson.getInt(key));
            }
            Level level = new Level(layout, time, spawnInterval, scoreIncreaseModifier, scoreDecreaseModifier, balls,
                    scoreIncrease, scoreDecrease);
            app.levels.add(level);

        }
    }

}
