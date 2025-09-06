package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.utils.objects.BiomeData;
import io.github.lumine1909.custombiomecolors.utils.objects.BiomeKey;
import org.bukkit.Location;

public interface NmsServer<Biome, Holder, ResourceKey> {

    NmsBiome<Biome, Holder, ResourceKey> getBiomeFromBiomeKey(BiomeKey biomeKey);

    NmsBiome<Biome, Holder, ResourceKey> getWrappedBiomeHolder(Holder biomeBase);

    boolean doesBiomeExist(BiomeKey biomeKey);

    NmsBiome<Biome, Holder, ResourceKey> createCustomBiome(BiomeData biomeData);

    void setBiomeAt(Location location, NmsBiome<Biome, Holder, ResourceKey> nmsBiome);

    Holder getBiomeAt(Location location);

    Holder registerBiome(Holder original, Biome biome, ResourceKey resourceKey);

    String getBiomeString(NmsBiome<Biome, Holder, ResourceKey> nmsBiome);
}
