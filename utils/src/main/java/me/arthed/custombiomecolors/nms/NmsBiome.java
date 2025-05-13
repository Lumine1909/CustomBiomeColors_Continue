package me.arthed.custombiomecolors.nms;

import me.arthed.custombiomecolors.utils.objects.BiomeColors;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;

public interface NmsBiome<Biome, Holder, ResourceKey> {

    Holder getBiomeHolder();

    Biome getBiome();

    BiomeColors getBiomeColors();

    NmsBiome<Biome, Holder, ResourceKey> cloneWithDifferentColors(NmsServer<Biome, Holder, ResourceKey> nmsServer, BiomeKey newBiomeKey, BiomeColors biomeColors);
}