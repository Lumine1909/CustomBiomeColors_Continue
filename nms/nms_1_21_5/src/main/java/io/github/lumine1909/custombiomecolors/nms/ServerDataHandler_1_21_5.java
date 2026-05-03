package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;

import java.util.Collection;

public class ServerDataHandler_1_21_5 implements ServerDataHandler<Biome, Holder<Biome>, ResourceKey<Biome>> {

    private static final MappedRegistry<Biome> BIOME_REGISTRY = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private static final Holder.Reference<Biome> PLAINS = BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("minecraft", "plains"))).orElseThrow();

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> getBiomeFromKey(BiomeKey biomeKey) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiome(biomeKey)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_5(BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).orElseThrow());
    }

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> wrapToAccessor(Holder<Biome> biomeBase) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiomeFromHolder(biomeBase)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_5(biomeBase);
    }

    public boolean hasBiome(BiomeKey biomeKey) {
        return BIOME_REGISTRY.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).isPresent();
    }

    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> createCustomBiome(BiomeData biomeData) {
        Holder<Biome> holder = BIOME_REGISTRY.get(ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(biomeData.baseBiomeKey().key(), biomeData.baseBiomeKey().value())
        )).orElse(PLAINS);

        Biome biome = holder.value();
        ColorData colorData = biomeData.colorData();
        BiomeKey biomeKey = biomeData.biomeKey();

        ResourceKey<Biome> resourceKey = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()));
        Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biome.getGenerationSettings())
            .mobSpawnSettings(biome.getMobSettings())
            .hasPrecipitation(biome.hasPrecipitation())
            .temperature(biome.climateSettings.temperature())
            .downfall(biome.climateSettings.downfall())
            .temperatureAdjustment(biome.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder();
        builder.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
            .waterColor(colorData.get(ColorType.WATER))
            .waterFogColor(colorData.get(ColorType.WATER_FOG))
            .skyColor(colorData.get(ColorType.SKY))
            .fogColor(colorData.get(ColorType.FOG));
        colorData.apply(ColorType.GRASS, builder::grassColorOverride);
        colorData.apply(ColorType.FOLIAGE, builder::foliageColorOverride);
        colorData.apply(ColorType.DRY_FOLIAGE, builder::dryFoliageColorOverride);

        biomeBuilder.specialEffects(builder.build());
        Biome customBiome = biomeBuilder.build();
        return new BiomeAccessor_1_21_5(this.registerBiome(holder, customBiome, resourceKey), biomeData);
    }

    @Override
    public MappedRegistry<Biome> getRegistry() {
        return BIOME_REGISTRY;
    }

    @Override
    public Collection<?> getTagList(Holder<Biome> original) {
        return original.tags().toList();
    }
}