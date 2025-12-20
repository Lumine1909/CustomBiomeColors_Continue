package io.github.lumine1909.custombiomecolors.object;

import io.github.lumine1909.custombiomecolors.nms.BiomeAccessor;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public record BiomeData(BiomeKey biomeKey, BiomeKey baseBiomeKey, ColorData colorData) {

    private static final Map<ColorData, BiomeAccessor> colorCache = new HashMap<>();
    private static final Map<BiomeKey, BiomeAccessor> keyCache = new HashMap<>();
    private static final Map<Object, BiomeAccessor> holderCache = new HashMap<>();

    public static BiomeAccessor getBiome(ColorData colorData) {
        return colorCache.get(colorData);
    }

    public static BiomeAccessor getBiome(BiomeKey biomeKey) {
        return keyCache.get(biomeKey);
    }

    public static BiomeAccessor getBiomeFromHolder(Object holder) {
        return holderCache.get(holder);
    }

    public static void updateBiome(ColorData colorData, BiomeAccessor biomeAccessor) {
        if (!colorCache.containsKey(colorData) || biomeAccessor.getBiomeData().biomeKey().toString().startsWith("cbc:")) {
            colorCache.put(colorData, biomeAccessor);
        }
        keyCache.put(biomeAccessor.getBiomeData().biomeKey(), biomeAccessor);
        holderCache.put(biomeAccessor.getBiomeHolder(), biomeAccessor);
    }
}