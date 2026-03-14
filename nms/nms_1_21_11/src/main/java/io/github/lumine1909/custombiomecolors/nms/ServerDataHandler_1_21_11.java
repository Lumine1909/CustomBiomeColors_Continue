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
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Collection;

public class ServerDataHandler_1_21_11 implements ServerDataHandler<Biome, Holder<Biome>, ResourceKey<Biome>> {

    private final MappedRegistry<Biome> biomeRegistry = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private final Holder.Reference<Biome> plains = biomeRegistry.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "plains"))).orElseThrow();

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> getBiomeFromKey(BiomeKey biomeKey) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiome(biomeKey)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_11(this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).orElseThrow());
    }

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> wrapToAccessor(Holder<Biome> biomeBase) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiomeFromHolder(biomeBase)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_11(biomeBase);
    }

    public boolean hasBiome(BiomeKey biomeKey) {
        return this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).isPresent();
    }

    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> createCustomBiome(BiomeData biomeData) {
        Holder<Biome> holder = this.biomeRegistry.get(ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(biomeData.baseBiomeKey().key(), biomeData.baseBiomeKey().value())
        )).orElse(plains);

        Biome biome = holder.value();
        ColorData colorData = biomeData.colorData();
        BiomeKey biomeKey = biomeData.biomeKey();

        ResourceKey<Biome> resourceKey = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()));
        Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biome.getGenerationSettings())
            .mobSpawnSettings(biome.getMobSettings())
            .hasPrecipitation(biome.hasPrecipitation())
            .temperature(biome.climateSettings.temperature())
            .downfall(biome.climateSettings.downfall())
            .temperatureAdjustment(biome.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder();
        builder.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE).waterColor(colorData.get(ColorType.WATER));
        colorData.applyNonNull(ColorType.GRASS, builder::grassColorOverride);
        colorData.applyNonNull(ColorType.FOLIAGE, builder::foliageColorOverride);
        colorData.applyNonNull(ColorType.DRY_FOLIAGE, builder::dryFoliageColorOverride);
        biomeBuilder.specialEffects(builder.build());

        EnvironmentAttributeMap.Builder attributesBuilder = EnvironmentAttributeMap.builder().putAll(biome.getAttributes());
        colorData.applyNonNull(ColorType.WATER_FOG, v -> attributesBuilder.set(EnvironmentAttributes.WATER_FOG_COLOR, v));
        colorData.applyNonNull(ColorType.SKY, v -> attributesBuilder.set(EnvironmentAttributes.SKY_COLOR, v));
        colorData.applyNonNull(ColorType.FOG, v -> attributesBuilder.set(EnvironmentAttributes.FOG_COLOR, v));
        colorData.applyNonNull(ColorType.SUNRISE_SUNSET, v -> attributesBuilder.set(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, v));
        colorData.applyNonNull(ColorType.CLOUD, v -> attributesBuilder.set(EnvironmentAttributes.CLOUD_COLOR, v));
        colorData.applyNonNull(ColorType.SKY_LIGHT, v -> attributesBuilder.set(EnvironmentAttributes.SKY_COLOR, v));
        biomeBuilder.putAttributes(attributesBuilder);

        Biome customBiome = biomeBuilder.build();
        return new BiomeAccessor_1_21_11(this.registerBiome(holder, customBiome, resourceKey), biomeData);
    }

    @Override
    public MappedRegistry<Biome> getRegistry() {
        return biomeRegistry;
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
        return new ColorData.Builder()
            .set(ColorType.SKY, attributes.getValue(EnvironmentAttributes.SKY_COLOR, vec3))
            .set(ColorType.FOG, attributes.getValue(EnvironmentAttributes.FOG_COLOR, vec3))
            .set(ColorType.WATER_FOG, attributes.getValue(EnvironmentAttributes.WATER_FOG_COLOR, vec3))
            .set(ColorType.CLOUD, attributes.getValue(EnvironmentAttributes.CLOUD_COLOR, vec3))
            .set(ColorType.SUNRISE_SUNSET, attributes.getValue(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, vec3))
            .set(ColorType.SKY_LIGHT, attributes.getValue(EnvironmentAttributes.SKY_LIGHT_COLOR, vec3))
            .build();
    }
}