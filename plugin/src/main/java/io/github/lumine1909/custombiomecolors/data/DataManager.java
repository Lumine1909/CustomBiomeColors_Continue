package io.github.lumine1909.custombiomecolors.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.data.adapter.BiomeDataAdapter;
import io.github.lumine1909.custombiomecolors.data.adapter.BiomeKeyAdapter;
import io.github.lumine1909.custombiomecolors.nms.NmsBiome;
import io.github.lumine1909.custombiomecolors.util.object.BiomeData;
import io.github.lumine1909.custombiomecolors.util.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.util.object.ColorData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public class DataManager {

    private final CustomBiomeColors plugin = CustomBiomeColors.getInstance();
    private final File file;
    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
    private final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(BiomeKey.class, new BiomeKeyAdapter())
        .registerTypeAdapter(BiomeData.class, new BiomeDataAdapter())
        .create();

    private Map<BiomeKey, BiomeData> biomeDataMap;

    public DataManager(String fileName) {
        this.file = new File(this.plugin.getDataFolder(), fileName);
        if (!this.file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
        try (FileReader reader = new FileReader(this.file)) {
            Type typeToken = new TypeToken<Map<BiomeKey, BiomeData>>() {}.getType();
            this.biomeDataMap = gson.fromJson(reader, typeToken);
        } catch (Exception e) {
            plugin.getSLF4JLogger().warn("It seems you are using an legacy data format, start converting...");
            DataConverter converter = new DataConverter(this.file);
            this.biomeDataMap = converter.convert();
            scheduleSave();
        }
        if (this.biomeDataMap == null) {
            this.biomeDataMap = new HashMap<>();
        }
    }

    private void save0() throws IOException {
        final String json = gson.toJson(biomeDataMap);
        Files.write(this.file.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void saveBiome(BiomeKey biomeKey, BiomeData biomeData) {
        this.biomeDataMap.put(biomeKey, biomeData);
        scheduleSave();
    }

    private Future<?> scheduleSave() {
        return saveExecutor.submit(() -> {
            try {
                save0();
            } catch (Exception e) {
                plugin.getSLF4JLogger().error("Failed to save data", e);
            }
        });
    }

    @NotNull
    public NmsBiome getBiomeByColorOrElse(boolean forceKey, ColorData colorData, Supplier<NmsBiome> orElse) {
        NmsBiome biome;
        if (forceKey || (biome = BiomeData.getBiome(colorData)) == null || !biome.getBiomeData().biomeKey().toString().startsWith("cbc:")) {
            biome = orElse.get();
            saveBiome(biome.getBiomeData().biomeKey(), biome.getBiomeData());
            plugin.getPacketHandler().updateCache(biome.getBiomeData().biomeKey().toString(), System.currentTimeMillis() + 1000);
        }
        return biome;
    }

    public void loadBiomes() {
        for (var entry : this.biomeDataMap.entrySet()) {
            plugin.getNmsServer().createCustomBiome(entry.getValue());
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