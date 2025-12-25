package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.IdentityHashMap;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public class ServerDataHandler_1_21_9 implements ServerDataHandler<Biome, Holder<Biome>, ResourceKey<Biome>> {

    private final MappedRegistry<Biome> biomeRegistry = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private final Holder.Reference<Biome> plains = biomeRegistry.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("minecraft", "plains"))).orElseThrow();

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> getBiomeFromKey(BiomeKey biomeKey) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiome(biomeKey)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_9(this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).orElseThrow());
    }

    @SuppressWarnings("unchecked")
    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> wrapToAccessor(Holder<Biome> biomeBase) {
        BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiomeFromHolder(biomeBase)) != null) {
            return biome;
        }
        return new BiomeAccessor_1_21_9(biomeBase);
    }

    public boolean hasBiome(BiomeKey biomeKey) {
        return this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).isPresent();
    }

    public BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> createCustomBiome(BiomeData biomeData) {
        Holder<Biome> holder = this.biomeRegistry.get(ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(biomeData.baseBiomeKey().key(), biomeData.baseBiomeKey().value())
        )).orElse(plains);

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

        if (colorData.has(ColorType.GRASS)) {
            builder.grassColorOverride(colorData.get(ColorType.GRASS));
        }
        if (colorData.has(ColorType.FOLIAGE)) {
            builder.foliageColorOverride(colorData.get(ColorType.FOLIAGE));
        }
        if (colorData.has(ColorType.DRY_FOLIAGE)) {
            builder.dryFoliageColorOverride(colorData.get(ColorType.DRY_FOLIAGE));
        }

        biomeBuilder.specialEffects(builder.build());
        Biome customBiome = biomeBuilder.build();
        return new BiomeAccessor_1_21_9(this.registerBiome(holder, customBiome, resourceKey), biomeData);
    }

    public void setBiomeAt(Location location, BiomeAccessor<Biome, Holder<Biome>, ResourceKey<Biome>> biomeAccessor) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        chunk.setBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2, biomeAccessor.getBiomeHolder());
    }

    public Holder<Biome> getBiomeAt(Location location) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        return chunk.getNoiseBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2);
    }

    public Holder<Biome> registerBiome(Holder<Biome> original, Biome biome, ResourceKey<Biome> resourceKey) {
        try {
            field$MappedRegistry$frozen.set(this.biomeRegistry, false);
            field$MappedRegistry$unregisteredIntrusiveHolders.set(this.biomeRegistry, new IdentityHashMap<>());

            this.biomeRegistry.createIntrusiveHolder(biome);
            Holder<Biome> holder = this.biomeRegistry.register(resourceKey, biome, RegistrationInfo.BUILT_IN);
            method$Holder$bindTags.invoke(holder, original.tags().toList());

            field$MappedRegistry$unregisteredIntrusiveHolders.set(this.biomeRegistry, null);
            field$MappedRegistry$frozen.set(this.biomeRegistry, true);

            return holder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}