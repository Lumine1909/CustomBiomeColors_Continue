package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.jetbrains.annotations.NotNull;

public class NmsBiome_1_21_11 extends NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> {

    public NmsBiome_1_21_11(Holder<@NotNull Biome> biomeHolder) {
        this(biomeHolder, fetchNmsBiomeData(biomeHolder));
    }

    public NmsBiome_1_21_11(Holder<@NotNull Biome> biomeHolder, BiomeData cachedData) {
        super(biomeHolder, biomeHolder.value(), cachedData);
    }

    private static BiomeData fetchNmsBiomeData(Holder<@NotNull Biome> nmsBiome) {
        BiomeSpecialEffects specialEffects = nmsBiome.value().getSpecialEffects();
        EnvironmentAttributeMap attributes = nmsBiome.value().getAttributes();
        ColorData colorData = new ColorData()
            .set(ColorType.GRASS, specialEffects.grassColorOverride().orElse(null))
            .set(ColorType.FOLIAGE, specialEffects.foliageColorOverride().orElse(null))
            .set(ColorType.DRY_FOLIAGE, specialEffects.dryFoliageColorOverride().orElse(null))
            .set(ColorType.WATER, specialEffects.waterColor())
            .set(ColorType.WATER_FOG, getData(attributes.get(EnvironmentAttributes.WATER_FOG_COLOR)))
            .set(ColorType.SKY, getData(attributes.get(EnvironmentAttributes.SKY_COLOR)))
            .set(ColorType.FOG, getData(attributes.get(EnvironmentAttributes.FOG_COLOR)))
            .set(ColorType.SUNRISE_SUNSET, getData(attributes.get(EnvironmentAttributes.SUNRISE_SUNSET_COLOR)))
            .set(ColorType.CLOUD, getData(attributes.get(EnvironmentAttributes.CLOUD_COLOR)))
            .set(ColorType.SKY_LIGHT, getData(attributes.get(EnvironmentAttributes.SKY_LIGHT_COLOR)));
        BiomeKey biomeKey = BiomeKey.fromString(nmsBiome.getRegisteredName());
        return new BiomeData(biomeKey, biomeKey, colorData);
    }

    private static Integer getData(EnvironmentAttributeMap.Entry<@NotNull Integer, ?> entry) {
        return entry == null ? null : entry.applyModifier(0);
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