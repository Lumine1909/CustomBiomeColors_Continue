package io.github.lumine1909.custombiomecolors.object;

import io.github.lumine1909.custombiomecolors.nms.NmsBiome;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public record BiomeData(BiomeKey biomeKey, BiomeKey baseBiomeKey, ColorData colorData) {

    private static final Map<ColorData, NmsBiome> colorCache = new HashMap<>();
    private static final Map<BiomeKey, NmsBiome> keyCache = new HashMap<>();
    private static final Map<Object, NmsBiome> holderCache = new HashMap<>();

    public static NmsBiome getBiome(ColorData colorData) {
        return colorCache.get(colorData);
    }

    public static NmsBiome getBiome(BiomeKey biomeKey) {
        return keyCache.get(biomeKey);
    }

    public static NmsBiome getBiomeFromHolder(Object holder) {
        return holderCache.get(holder);
    }

    public static void updateBiome(ColorData colorData, NmsBiome nmsBiome) {
        if (!colorCache.containsKey(colorData) || nmsBiome.getBiomeData().biomeKey().toString().startsWith("cbc:")) {
            colorCache.put(colorData, nmsBiome);
        }
        keyCache.put(nmsBiome.getBiomeData().biomeKey(), nmsBiome);
        holderCache.put(nmsBiome.getBiomeHolder(), nmsBiome);
    }
}