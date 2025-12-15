package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jetbrains.annotations.NotNull;


public class NmsBiome_1_21_3 extends NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> {

    public NmsBiome_1_21_3(Holder<Biome> biomeHolder) {
        this(biomeHolder, fetchNmsBiomeData(biomeHolder));
    }

    public NmsBiome_1_21_3(Holder<Biome> biomeHolder, BiomeData cachedData) {
        super(biomeHolder, biomeHolder.value(), cachedData);
    }

    private static BiomeData fetchNmsBiomeData(Holder<Biome> nmsBiome) {
        BiomeSpecialEffects specialEffects = nmsBiome.value().getSpecialEffects();
        ColorData colorData = new ColorData()
            .set(ColorType.GRASS, specialEffects.getGrassColorOverride().orElse(null))
            .set(ColorType.FOLIAGE, specialEffects.getFoliageColorOverride().orElse(null))
            .set(ColorType.WATER, specialEffects.getWaterColor())
            .set(ColorType.WATER_FOG, specialEffects.getWaterFogColor())
            .set(ColorType.SKY, specialEffects.getSkyColor())
            .set(ColorType.FOG, specialEffects.getFogColor());
        BiomeKey biomeKey = BiomeKey.fromString(nmsBiome.getRegisteredName());
        return new BiomeData(biomeKey, biomeKey, colorData);
    }

    public NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> cloneWithDifferentColor(NmsServer<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> nmsServer, BiomeKey newBiomeKey, ColorData colorData) {
        BiomeData data = getBiomeData();
        return nmsServer.createCustomBiome(new BiomeData(newBiomeKey, data.baseBiomeKey(), colorData));
    }

    @Override
    public float getTemperature() {
        return biomeBase.climateSettings.temperature();
    }

    @Override
    public float getHumidity() {
        return biomeBase.climateSettings.downfall();
    }
}