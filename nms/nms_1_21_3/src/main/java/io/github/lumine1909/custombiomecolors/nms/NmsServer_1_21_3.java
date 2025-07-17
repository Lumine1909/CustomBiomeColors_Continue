package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.utils.objects.BiomeData;
import io.github.lumine1909.custombiomecolors.utils.objects.BiomeKey;
import io.github.lumine1909.custombiomecolors.utils.objects.ColorData;
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

import static io.github.lumine1909.custombiomecolors.utils.Reflection.*;

public class NmsServer_1_21_3 implements NmsServer<Biome, Holder<Biome>, ResourceKey<Biome>> {

    private final MappedRegistry<Biome> biomeRegistry = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();

    @SuppressWarnings("unchecked")
    public NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> getBiomeFromBiomeKey(BiomeKey biomeKey) {
        NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiome(biomeKey)) != null) {
            return biome;
        }
        return new NmsBiome_1_21_3(this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).orElseThrow());
    }

    @SuppressWarnings("unchecked")
    public NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> getWrappedBiomeHolder(Holder<Biome> biomeBase) {
        NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> biome;
        if ((biome = BiomeData.getBiomeFromHolder(biomeBase)) != null) {
            return biome;
        }
        return new NmsBiome_1_21_3(biomeBase);
    }

    public boolean doesBiomeExist(BiomeKey biomeKey) {
        return this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).isPresent();
    }

    public NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> createCustomBiome(BiomeData biomeData) {
        Holder<Biome> biomeHolder = this.biomeRegistry.get(ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(biomeData.baseBiomeKey().key(), biomeData.baseBiomeKey().value())
        )).orElseThrow();

        Biome biomeBase = biomeHolder.value();
        ColorData colorData = biomeData.colorData();
        BiomeKey biomeKey = biomeData.biomeKey();

        ResourceKey<Biome> customBiomeKey = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()));
        Biome.BiomeBuilder customBiomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biomeBase.getGenerationSettings())
            .mobSpawnSettings(biomeBase.getMobSettings())
            .hasPrecipitation(biomeBase.hasPrecipitation())
            .temperature(biomeBase.climateSettings.temperature())
            .downfall(biomeBase.climateSettings.downfall())
            .temperatureAdjustment(biomeBase.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder customBiomeColors = new BiomeSpecialEffects.Builder();
        customBiomeColors.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
            .waterColor(colorData.waterColor())
            .waterFogColor(colorData.waterFogColor())
            .skyColor(colorData.skyColor())
            .fogColor(colorData.fogColor());

        if (colorData.grassColor().isPresent()) {
            customBiomeColors.grassColorOverride(colorData.grassColor().get());
        }
        if (colorData.foliageColor().isPresent()) {
            customBiomeColors.foliageColorOverride(colorData.foliageColor().get());
        }

        customBiomeBuilder.specialEffects(customBiomeColors.build());
        Biome customBiome = customBiomeBuilder.build();
        return new NmsBiome_1_21_3(this.registerBiome(biomeHolder, customBiome, customBiomeKey), biomeData);
    }

    public void setBiomeAt(Location location, NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> nmsBiome) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        chunk.setBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2, nmsBiome.getBiomeHolder());
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
        } catch (Exception error) {
            error.printStackTrace();
        }
        return null;
    }

    public String getBiomeString(NmsBiome<Biome, Holder<Biome>, ResourceKey<Biome>> nmsBiome) {
        ResourceLocation resourceLocation = this.biomeRegistry.getKey(nmsBiome.getBiome());
        return resourceLocation == null ? "minecraft:plain" : resourceLocation.toString();
    }
}