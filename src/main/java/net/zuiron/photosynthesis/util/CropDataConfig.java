package net.zuiron.photosynthesis.util;

import com.google.gson.*;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.zuiron.photosynthesis.Photosynthesis;
import net.zuiron.photosynthesis.config.CropConfig;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class CropDataConfig implements SimpleSynchronousResourceReloadListener {

    public static HashMap<Identifier, CropConfig> AllCropConfigMap = new HashMap<>();

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


                AllCropConfigMap.put(cropIdentifier, new CropConfig(maxAge,minAge,biomesTemperature,biomesHumidity));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Photosynthesis.LOGGER.info("Crop:"+ cropIdentifier.toString());
            Photosynthesis.LOGGER.info("|__maxAge:"+ Arrays.toString(AllCropConfigMap.get(cropIdentifier).getMaxAge()));
            Photosynthesis.LOGGER.info("|__minAge:"+ Arrays.toString(AllCropConfigMap.get(cropIdentifier).getMinAge()));
            Photosynthesis.LOGGER.info("|__biomes temp:"+Arrays.toString(AllCropConfigMap.get(cropIdentifier).getBiomesTemperature()));
            Photosynthesis.LOGGER.info("|__biomes humidity:"+Arrays.toString(AllCropConfigMap.get(cropIdentifier).getBiomesHumidity()));

        }
        if(!AllCropConfigMap.isEmpty()) {
            Photosynthesis.LOGGER.info("Successfully loaded "+AllCropConfigMap.size()+" custom crop configs.");
        }else {
            Photosynthesis.LOGGER.info("No crop configs.");
        }

    }
}

