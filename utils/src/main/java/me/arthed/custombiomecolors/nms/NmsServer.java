package me.arthed.custombiomecolors.nms;

import me.arthed.custombiomecolors.utils.objects.BiomeColors;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import org.bukkit.block.Block;

public interface NmsServer<Biome, Holder, ResourceKey> {

    NmsBiome<Biome, Holder, ResourceKey> getBiomeFromBiomeKey(BiomeKey biomeKey);

    NmsBiome<Biome, Holder, ResourceKey> getWrappedBiomeHolder(Holder biomeBase);

    boolean doesBiomeExist(BiomeKey biomeKey);

    Holder createCustomBiome(BiomeKey biomeKey, BiomeColors biomeColors);

    void setBlockBiome(Block block, NmsBiome<Biome, Holder, ResourceKey> nmsBiome);

    Holder getBlocksBiome(Block block);

    Holder registerBiome(Holder original, Biome biome, ResourceKey resourceKey);

    String getBiomeString(NmsBiome<Biome, Holder, ResourceKey> nmsBiome);
}
