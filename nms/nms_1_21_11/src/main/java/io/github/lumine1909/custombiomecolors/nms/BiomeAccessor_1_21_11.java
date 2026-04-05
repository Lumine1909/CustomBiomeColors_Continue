package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import static io.github.lumine1909.custombiomecolors.nms.ServerDataHandler_1_21_11.COLOR_ATTRIBUTE;

public class BiomeAccessor_1_21_11 extends BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> {

    public BiomeAccessor_1_21_11(Holder<Biome> biomeHolder) {
        this(biomeHolder, fetchNmsBiomeData(biomeHolder));
    }

    public BiomeAccessor_1_21_11(Holder<Biome> biomeHolder, BiomeData cachedData) {
        super(biomeHolder, biomeHolder.value(), cachedData);
    }

    private static BiomeData fetchNmsBiomeData(Holder<Biome> nmsBiome) {
        BiomeSpecialEffects specialEffects = nmsBiome.value().getSpecialEffects();
        EnvironmentAttributeMap attributes = nmsBiome.value().getAttributes();
        ColorData.Builder builder = new ColorData.Builder()
            .set(ColorType.GRASS, specialEffects.grassColorOverride().orElse(null))
            .set(ColorType.FOLIAGE, specialEffects.foliageColorOverride().orElse(null))
            .set(ColorType.DRY_FOLIAGE, specialEffects.dryFoliageColorOverride().orElse(null))
            .set(ColorType.WATER, specialEffects.waterColor());
        COLOR_ATTRIBUTE.forEach((color, attribute) -> builder.set(color, getData(attributes.get(attribute))));
        BiomeKey biomeKey = BiomeKey.fromString(nmsBiome.getRegisteredName());
        return new BiomeData(biomeKey, biomeKey, builder.build());
    }

    private static Integer getData(EnvironmentAttributeMap.Entry<Integer, ?> entry) {
        return entry == null ? null : entry.applyModifier(0);
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