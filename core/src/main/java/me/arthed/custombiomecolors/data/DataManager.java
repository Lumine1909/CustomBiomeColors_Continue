package me.arthed.custombiomecolors.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import me.arthed.custombiomecolors.CustomBiomeColors;
import me.arthed.custombiomecolors.nms.NmsBiome;
import me.arthed.custombiomecolors.utils.objects.BiomeColors;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@SuppressWarnings("rawtypes")
public class DataManager {

    private final CustomBiomeColors plugin = CustomBiomeColors.getInstance();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file;
    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
    private Map<String, int[]> map = new ConcurrentHashMap<>();

    public DataManager(String fileName) {
        this.file = new File(this.plugin.getDataFolder(), fileName);
        if (!this.file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
        try {
            Type typeToken = new TypeToken<Map<String, int[]>>() {
            }.getType();
            this.map = gson.fromJson(new FileReader(this.file), typeToken);
            if (this.map == null) {
                this.map = new HashMap<>();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void save0() throws IOException {
        final String json = gson.toJson(map);
        Files.write(this.file.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void saveBiome(BiomeKey biomeKey, BiomeColors biomeColors) {
        this.map.put(biomeKey.key + ":" + biomeKey.value, new int[]{
            biomeColors.getGrassColor(),
            biomeColors.getFoliageColor(),
            biomeColors.getWaterColor(),
            biomeColors.getWaterFogColor(),
            biomeColors.getSkyColor(),
            biomeColors.getFogColor()
        });
        scheduleSave();
    }

    private Future<?> scheduleSave() {
        return saveExecutor.submit(() -> {
            try {
                save0();
            } catch (IOException e) {
                plugin.getSLF4JLogger().error("Failed to save data", e);
            }
        });
    }

    @Nullable
    public NmsBiome getBiomeWithSpecificColors(BiomeColors biomeColors) {
        for (Map.Entry<String, int[]> entry : this.map.entrySet()) {
            int[] colors = entry.getValue();
            if (colors[0] == biomeColors.getGrassColor() &&
                colors[1] == biomeColors.getFoliageColor() &&
                colors[2] == biomeColors.getWaterColor() &&
                colors[3] == biomeColors.getWaterFogColor() &&
                colors[4] == biomeColors.getSkyColor() &&
                colors[5] == biomeColors.getFogColor()) {
                return plugin.getNmsServer().getBiomeFromBiomeKey(new BiomeKey(entry.getKey()));
            }
        }
        return null;
    }

    public void loadBiomes() {
        for (Map.Entry<String, int[]> entry : this.map.entrySet()) {
            int[] colors = entry.getValue();
            plugin.getNmsServer().createCustomBiome(
                new BiomeKey(entry.getKey()),
                new BiomeColors()
                    .setGrassColor(colors[0])
                    .setFoliageColor(colors[1])
                    .setWaterColor(colors[2])
                    .setWaterFogColor(colors[3])
                    .setSkyColor(colors[4])
                    .setFogColor(colors[5])
            );
        }
    }

    public void saveOnClose() {
        Future<?> future = scheduleSave();
        saveExecutor.shutdown();
        try {
            future.get(30, TimeUnit.SECONDS);
            if (!saveExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                plugin.getSLF4JLogger().warn("Data save executor did not shut down cleanly");
            }
        } catch (Exception e) {
            plugin.getSLF4JLogger().error("Failed during shutdown save process", e);
        }
    }
}
