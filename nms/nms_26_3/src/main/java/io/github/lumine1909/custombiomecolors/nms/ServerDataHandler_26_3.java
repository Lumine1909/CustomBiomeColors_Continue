package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.util.Collection;
import java.util.Map;

public class ServerDataHandler_26_3 implements ServerDataHandler<Biome, Holder<Biome>, ResourceKey<Biome>> {

    static final Map<ColorType, EnvironmentAttribute<Vector3fc>> COLOR_ATTRIBUTE_VEC3 = Map.of(
        ColorType.SKY, EnvironmentAttributes.SKY_COLOR,
        ColorType.FOG, EnvironmentAttributes.FOG_COLOR,
        ColorType.WATER_FOG, EnvironmentAttributes.WATER_FOG_COLOR,
        ColorType.SKY_LIGHT, EnvironmentAttributes.SKY_LIGHT_COLOR
    );
    static final Map<ColorType, EnvironmentAttribute<Vector4fc>> COLOR_ATTRIBUTE_VEC4 = Map.of(
        ColorType.CLOUD, EnvironmentAttributes.CLOUD_COLOR,
        ColorType.SUNRISE_SUNSET, EnvironmentAttributes.SUNRISE_SUNSET_COLOR
    );
    private static final MappedRegistry<Biome> BIOME_REGISTRY = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private static final Holder.Reference<Biome> PLAINS = BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "plains"))).orElseThrow();

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> getBiomeFromKey(BiomeKey biomeKey) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiome(biomeKey)) != null) {
            return biome;
        }
        return new BiomeAccessor_26_3(BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.namespace(), biomeKey.value()))).orElseThrow());
    }

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> wrapToAccessor(Holder<Biome> biomeBase) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiomeFromHolder(biomeBase)) != null) {
            return biome;
        }
        return new BiomeAccessor_26_3(biomeBase);
    }

    public boolean hasBiome(BiomeKey biomeKey) {
        return BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.namespace(), biomeKey.value()))).isPresent();
    }

    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> createCustomBiome(BiomeData biomeData, boolean register) {
        Holder<Biome> holder = BIOME_REGISTRY.get(ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(biomeData.baseBiomeKey().namespace(), biomeData.baseBiomeKey().value())
        )).orElse(PLAINS);

        Biome biome = holder.value();
        ColorData colorData = biomeData.colorData();
        BiomeKey biomeKey = biomeData.biomeKey();

        ResourceKey<Biome> resourceKey = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.namespace(), biomeKey.value()));
        Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biome.getGenerationSettings())
            .mobSpawnSettings(biome.getAttributes().get(EnvironmentAttributes.NATURAL_MOB_SPAWNS).applyModifier(MobSpawnSettings.EMPTY)) // Nullable???
            .hasPrecipitation(biome.hasPrecipitation())
            .temperature(biome.climateSettings.temperature())
            .downfall(biome.climateSettings.downfall())
            .temperatureAdjustment(biome.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder();
        builder.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE).waterColor(colorData.get(ColorType.WATER));
        colorData.apply(ColorType.GRASS, builder::grassColorOverride);
        colorData.apply(ColorType.FOLIAGE, builder::foliageColorOverride);
        colorData.apply(ColorType.DRY_FOLIAGE, builder::dryFoliageColorOverride);
        biomeBuilder.specialEffects(builder.build());
        biomeBuilder.specialEffects(builder.build());
        EnvironmentAttributeMap.Builder attributesBuilder = EnvironmentAttributeMap.builder().putAll(biome.getAttributes());
        COLOR_ATTRIBUTE_VEC3.forEach((color, attribute) -> {
            EnvironmentAttributeMap.Entry<Vector3fc, ?> entry = biome.getAttributes().get(attribute);
            Vector3fc defaultValue = entry == null ? null : entry.applyModifier(new Vector3f());
            colorData.apply(color, v -> attributesBuilder.set(attribute, ARGB.vector3fFromRGB24(v)), defaultValue == null ? null : ARGB.colorFromVector3f(defaultValue));
        });
        COLOR_ATTRIBUTE_VEC4.forEach((color, attribute) -> {
            EnvironmentAttributeMap.Entry<Vector4fc, ?> entry = biome.getAttributes().get(attribute);
            Vector4fc defaultValue = entry == null ? null : entry.applyModifier(new Vector4f());
            colorData.apply(color, v -> attributesBuilder.set(attribute, ARGB.vector4fFromARGB32(v)), defaultValue == null ? null : ARGB.colorFromVector4f(defaultValue));
        });
        biomeBuilder.putAttributes(attributesBuilder);
        Biome customBiome = biomeBuilder.build();

        return register
            ? new BiomeAccessor_26_3(this.registerBiome(holder, customBiome, resourceKey), biomeData)
            : new BiomeAccessor_26_3(customBiome, biomeData);
    }

    @Override
    public MappedRegistry<Biome> getRegistry() {
        return BIOME_REGISTRY;
    }

    @Override
    public Collection<?> getTagList(Holder<Biome> original) {
        return original.tags().toList();
    }

    @Override
    public ColorData getDimensionColor(Location location) {
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        EnvironmentAttributeSystem attributes = level.environmentAttributes();
        Vec3 vec3 = new Vec3(location.x(), location.y(), location.z());
        ColorData.Builder builder = new ColorData.Builder();
        COLOR_ATTRIBUTE_VEC3.forEach((color, attribute) -> builder.set(color, ARGB.colorFromVector3f(attributes.getValue(attribute, vec3))));
        COLOR_ATTRIBUTE_VEC4.forEach((color, attribute) -> builder.set(color, ARGB.colorFromVector4f(attributes.getValue(attribute, vec3))));
        return builder.build();
    }
}