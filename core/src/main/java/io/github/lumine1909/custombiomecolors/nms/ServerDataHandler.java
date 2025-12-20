package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import org.bukkit.Location;

public interface ServerDataHandler<Biome, Holder, ResourceKey> extends DimensionDataAccessor {

    BiomeAccessor<Biome, Holder, ResourceKey> getBiomeFromBiomeKey(BiomeKey biomeKey);

    BiomeAccessor<Biome, Holder, ResourceKey> wrapToAccessor(Holder biomeBase);

    boolean hasBiome(BiomeKey biomeKey);

    BiomeAccessor<Biome, Holder, ResourceKey> createCustomBiome(BiomeData biomeData);

    void setBiomeAt(Location location, BiomeAccessor<Biome, Holder, ResourceKey> biomeAccessor);

    Holder getBiomeAt(Location location);

    Holder registerBiome(Holder original, Biome biome, ResourceKey resourceKey);

    String getBiomeId(BiomeAccessor<Biome, Holder, ResourceKey> biomeAccessor);
}
