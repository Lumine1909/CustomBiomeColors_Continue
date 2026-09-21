package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.ToIntFunction;

import static io.github.lumine1909.custombiomecolors.nms.ServerDataHandler_26_3.COLOR_ATTRIBUTE_VEC3;
import static io.github.lumine1909.custombiomecolors.nms.ServerDataHandler_26_3.COLOR_ATTRIBUTE_VEC4;

public class BiomeAccessor_26_3 extends BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> {

    public BiomeAccessor_26_3(Holder<Biome> biomeHolder) {
        this(biomeHolder, fetchNmsBiomeData(biomeHolder));
    }

    public BiomeAccessor_26_3(Holder<Biome> biomeHolder, BiomeData cachedData) {
        super(biomeHolder, biomeHolder.value(), cachedData);
    }

    public BiomeAccessor_26_3(Biome biome, BiomeData cachedData) {
        super(biome, cachedData);
    }

    private static BiomeData fetchNmsBiomeData(Holder<Biome> nmsBiome) {
        BiomeSpecialEffects specialEffects = nmsBiome.value().getSpecialEffects();
        EnvironmentAttributeMap attributes = nmsBiome.value().getAttributes();
        ColorData.Builder builder = new ColorData.Builder()
            .set(ColorType.GRASS, specialEffects.grassColorOverride().orElse(null))
            .set(ColorType.FOLIAGE, specialEffects.foliageColorOverride().orElse(null))
            .set(ColorType.DRY_FOLIAGE, specialEffects.dryFoliageColorOverride().orElse(null))
            .set(ColorType.WATER, specialEffects.waterColor());
        COLOR_ATTRIBUTE_VEC3.forEach((color, attribute) -> builder.set(color, getData(attributes.get(attribute), new Vector3f(), ARGB::colorFromVector3f)));
        COLOR_ATTRIBUTE_VEC4.forEach((color, attribute) -> builder.set(color, getData(attributes.get(attribute), new Vector4f(), ARGB::colorFromVector4f)));
        BiomeKey biomeKey = BiomeKey.fromString(nmsBiome.getRegisteredName());
        return new BiomeData(biomeKey, biomeKey, builder.build());
    }

    private static <T> Integer getData(EnvironmentAttributeMap.Entry<T, ?> entry, T defaultValue, ToIntFunction<T> function) {
        return entry == null ? null : function.applyAsInt(entry.applyModifier(defaultValue));
    }

    @Override
    public float getTemperature() {
        return biome.climateSettings.temperature();
    }

    @Override
    public float getHumidity() {
        return biome.climateSettings.downfall();
    }
}