package me.arthed.custombiomecolors.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.arthed.custombiomecolors.CustomBiomeColors;
import me.arthed.custombiomecolors.utils.objects.BiomeData;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import me.arthed.custombiomecolors.utils.objects.ColorData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DataConverter {

    private final Map<String, int[]> rawMap = new ConcurrentHashMap<>();

    public DataConverter(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type typeToken = new TypeToken<Map<String, int[]>>() {
        }.getType();

        if (!file.exists()) {
            CustomBiomeColors.getInstance().getSLF4JLogger().warn("Data file {} does not exist, skipping conversion.", file.getAbsolutePath());
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Map<String, int[]> loaded = gson.fromJson(reader, typeToken);
            if (loaded != null) {
                rawMap.putAll(loaded);
            }
        } catch (IOException e) {
            CustomBiomeColors.getInstance().getSLF4JLogger().error("Failed to load data from {}", file.getAbsolutePath(), e);
        }
    }

    public Map<BiomeKey, BiomeData> convert() {
        Map<BiomeKey, BiomeData> biomeDataMap = new ConcurrentHashMap<>();
        BiomeKey fallbackPlains = BiomeKey.fromString("minecraft:plains");

        for (var entry : rawMap.entrySet()) {
            String keyStr = entry.getKey();
            int[] color = entry.getValue();

            if (color == null || color.length < 6) {
                CustomBiomeColors.getInstance().getSLF4JLogger().warn("Skipping invalid color data for biome {}", keyStr);
                continue;
            }

            BiomeKey biomeKey = BiomeKey.fromString(keyStr);
            ColorData colorData = new ColorData(
                color[5], color[2], color[3], color[4],
                color[1] != 0 ? Optional.of(color[1]) : Optional.empty(),
                Optional.empty(),
                color[0] != 0 ? Optional.of(color[0]) : Optional.empty()
            );
            BiomeData biomeData = new BiomeData(
                biomeKey,
                fallbackPlains,
                colorData
            );

            biomeDataMap.put(biomeKey, biomeData);
        }

        return biomeDataMap;
    }
}
