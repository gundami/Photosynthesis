package net.zuiron.photosynthesis.util;

import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.api.CropData;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static net.zuiron.photosynthesis.api.CropData.cropDataMap;

public class CropDataConfig implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return new Identifier(Photosynthesis.MOD_ID, "crops");
    }

    @Override
    public void reload(ResourceManager manager) {
        Set<Identifier> col = manager.findResources("crops", path -> path.getPath().endsWith(".json")).keySet();
        Photosynthesis.LOGGER.info("Attempting to register " + col.size() + " crops definitions... ");

        for (Identifier id : col) {
            String[] split = id.getPath().split("/");
            Identifier cropIdentifier = new Identifier(id.getNamespace(), split[split.length-1].replace(".json",""));
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {

                String data = IOUtils.toString(stream, StandardCharsets.UTF_8);
                JsonObject object = new Gson().fromJson(data, JsonObject.class);


                //MaxAge
                List<JsonElement> maxAgeList = object.getAsJsonArray("maxAge").asList();
                int[] maxAge = new int[4];
                for (int i = 0; i < 4; i++)
                    maxAge[i] = maxAgeList.get(i).getAsInt();
                //MinAge
                List<JsonElement> minAgeList = object.getAsJsonArray("minAge").asList();
                int[] minAge = new int[4];
                for (int i = 0; i < 4; i++)
                    minAge[i] = minAgeList.get(i).getAsInt();
                //Biomes Temperature
                List<JsonElement> biomesTemperatureList = object.getAsJsonArray("biomesTemperature").asList();
                float[] biomesTemperature = new float[2];
                for (int i = 0; i < 2; i++)
                    biomesTemperature[i] = biomesTemperatureList.get(i).getAsFloat();
                //Biomes Humidity
                List<JsonElement> biomesHumidityList = object.getAsJsonArray("biomesHumidity").asList();
                float[] biomesHumidity = new float[2];
                for (int i = 0; i < 2; i++)
                    biomesHumidity[i] = biomesHumidityList.get(i).getAsFloat();


                cropDataMap.put(cropIdentifier, new CropData(maxAge,minAge,biomesTemperature,biomesHumidity));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!cropDataMap.isEmpty()) {
            Photosynthesis.LOGGER.info("Successfully loaded "+cropDataMap.size()+" custom crop configs.");
        }else {
            Photosynthesis.LOGGER.info("No crop configs.");
        }

    }
}

