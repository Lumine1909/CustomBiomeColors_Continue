package io.github.lumine1909.custombiomecolors.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.data.adapter.BiomeDataAdapter;
import io.github.lumine1909.custombiomecolors.data.adapter.BiomeKeyAdapter;
import io.github.lumine1909.custombiomecolors.nms.BiomeAccessor;
import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
public class DataManager {

    private static final Type TYPE_TOKEN = new TypeToken<Map<BiomeKey, BiomeData>>() {
    }.getType();
    private static final Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(BiomeKey.class, new BiomeKeyAdapter())
        .registerTypeAdapter(BiomeData.class, new BiomeDataAdapter())
        .create();

    private final CustomBiomeColors plugin = CustomBiomeColors.getInstance();
    private final ExecutorService saveExecutor = Executors.newSingleThreadExecutor();
    private final File file;

    private Map<BiomeKey, BiomeData> biomeDataMap;

    public DataManager(String fileName) {
        this.file = new File(this.plugin.getDataFolder(), fileName);
        if (!this.file.exists()) {
            this.plugin.saveResource(fileName, false);
        }
        try (FileReader reader = new FileReader(this.file)) {
            this.biomeDataMap = gson.fromJson(reader, TYPE_TOKEN);
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
        Files.write(file.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public void saveBiome(BiomeKey biomeKey, BiomeData biomeData) {
        this.biomeDataMap.put(biomeKey, biomeData);
        plugin.getPacketHandler().updateCache(biomeKey.toString(), System.currentTimeMillis() + 1000);
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
    public BiomeAccessor getBiomeByColorOrElse(boolean forceKey, ColorData colorData, Supplier<BiomeAccessor> orElse) {
        BiomeAccessor biome;
        if (forceKey || (biome = BiomeData.getBiome(colorData)) == null || !biome.getBiomeData().biomeKey().toString().startsWith("cbc:")) {
            biome = orElse.get();
            saveBiome(biome.getBiomeData().biomeKey(), biome.getBiomeData());
        }
        return biome;
    }

    public void loadBiomes() {
        for (Map.Entry<BiomeKey, BiomeData> entry : this.biomeDataMap.entrySet()) {
            plugin.getServerDataHandler().createCustomBiome(entry.getValue(), true);
        }
    }

    public boolean hasBiome(BiomeKey biomeKey) {
        return biomeDataMap.containsKey(biomeKey);
    }

    public List<String> getAllBiomeId() {
        return biomeDataMap.keySet().stream().map(BiomeKey::toString).toList();
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