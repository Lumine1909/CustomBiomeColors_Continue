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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.IdentityHashMap;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public class NmsServer_1_21_11 implements NmsServer<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> {

    private final MappedRegistry<@NotNull Biome> biomeRegistry = (MappedRegistry<@NotNull Biome>) MinecraftServer.getServer().registryAccess().lookup(Registries.BIOME).orElseThrow();
    private final Holder.Reference<@NotNull Biome> plains = biomeRegistry.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "plains"))).orElseThrow();

    @SuppressWarnings("unchecked")
    public NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> getBiomeFromBiomeKey(BiomeKey biomeKey) {
        NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> biome;
        if ((biome = BiomeData.getBiome(biomeKey)) != null) {
            return biome;
        }
        return new NmsBiome_1_21_11(this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).orElseThrow());
    }

    @SuppressWarnings("unchecked")
    public NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> getWrappedBiomeHolder(Holder<@NotNull Biome> biomeBase) {
        NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> biome;
        if ((biome = BiomeData.getBiomeFromHolder(biomeBase)) != null) {
            return biome;
        }
        return new NmsBiome_1_21_11(biomeBase);
    }

    public boolean hasBiome(BiomeKey biomeKey) {
        return this.biomeRegistry.get(ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()))).isPresent();
    }

    public NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> createCustomBiome(BiomeData biomeData) {
        Holder<@NotNull Biome> holder = this.biomeRegistry.get(ResourceKey.create(
            Registries.BIOME,
            Identifier.fromNamespaceAndPath(biomeData.baseBiomeKey().key(), biomeData.baseBiomeKey().value())
        )).orElse(plains);

        Biome biome = holder.value();
        ColorData colorData = biomeData.colorData();
        BiomeKey biomeKey = biomeData.biomeKey();

        ResourceKey<@NotNull Biome> resourceKey = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(biomeKey.key(), biomeKey.value()));
        Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biome.getGenerationSettings())
            .mobSpawnSettings(biome.getMobSettings())
            .hasPrecipitation(biome.hasPrecipitation())
            .temperature(biome.climateSettings.temperature())
            .downfall(biome.climateSettings.downfall())
            .temperatureAdjustment(biome.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder builder = new BiomeSpecialEffects.Builder();
        builder.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE).waterColor(colorData.get(ColorType.WATER));

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

        EnvironmentAttributeMap.Builder attributesBuilder = EnvironmentAttributeMap.builder().putAll(biome.getAttributes());
        if (colorData.has(ColorType.WATER_FOG)) {
            attributesBuilder.set(EnvironmentAttributes.WATER_FOG_COLOR, colorData.get(ColorType.WATER_FOG));
        }
        if (colorData.has(ColorType.SKY)) {
            attributesBuilder.set(EnvironmentAttributes.SKY_COLOR, colorData.get(ColorType.SKY));
        }
        if (colorData.has(ColorType.FOG)) {
            attributesBuilder.set(EnvironmentAttributes.FOG_COLOR, colorData.get(ColorType.FOG));
        }
        if (colorData.has(ColorType.SUNRISE_SUNSET)) {
            attributesBuilder.set(EnvironmentAttributes.SUNRISE_SUNSET_COLOR, colorData.get(ColorType.SUNRISE_SUNSET));
        }
        if (colorData.has(ColorType.CLOUD)) {
            attributesBuilder.set(EnvironmentAttributes.CLOUD_COLOR, colorData.get(ColorType.CLOUD));
        }
        if (colorData.has(ColorType.SKY_LIGHT)) {
            attributesBuilder.set(EnvironmentAttributes.SKY_COLOR, colorData.get(ColorType.SKY_LIGHT));
        }

        biomeBuilder.putAttributes(attributesBuilder);
        Biome customBiome = biomeBuilder.build();
        return new NmsBiome_1_21_11(this.registerBiome(holder, customBiome, resourceKey), biomeData);
    }

    public void setBiomeAt(Location location, NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> nmsBiome) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        chunk.setBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2, nmsBiome.getBiomeHolder());
    }

    public Holder<@NotNull Biome> getBiomeAt(Location location) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        return chunk.getNoiseBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2);
    }

    public Holder<@NotNull Biome> registerBiome(Holder<@NotNull Biome> original, Biome
        biome, ResourceKey<@NotNull Biome> resourceKey) {
        try {
            field$MappedRegistry$frozen.set(this.biomeRegistry, false);
            field$MappedRegistry$unregisteredIntrusiveHolders.set(this.biomeRegistry, new IdentityHashMap<>());

            this.biomeRegistry.createIntrusiveHolder(biome);
            Holder<@NotNull Biome> holder = this.biomeRegistry.register(resourceKey, biome, RegistrationInfo.BUILT_IN);
            method$Holder$bindTags.invoke(holder, original.tags().toList());

            field$MappedRegistry$unregisteredIntrusiveHolders.set(this.biomeRegistry, null);
            field$MappedRegistry$frozen.set(this.biomeRegistry, true);

            return holder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBiomeId(NmsBiome<Biome, Holder<@NotNull Biome>, ResourceKey<@NotNull Biome>> nmsBiome) {
        Identifier id = this.biomeRegistry.getKey(nmsBiome.getBiome());
        return id == null ? "minecraft:plain" : id.toString();
    }
}