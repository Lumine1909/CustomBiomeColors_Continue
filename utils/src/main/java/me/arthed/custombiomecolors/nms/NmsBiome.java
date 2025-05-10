package me.arthed.custombiomecolors.nms;

import me.arthed.custombiomecolors.utils.objects.BiomeColors;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import net.minecraft.world.level.biome.Biome;

public interface NmsBiome {

    Biome getBiomeBase();

    BiomeColors getBiomeColors();

    NmsBiome cloneWithDifferentColors(NmsServer nmsServer, BiomeKey newBiomeKey, BiomeColors biomeColors);
}