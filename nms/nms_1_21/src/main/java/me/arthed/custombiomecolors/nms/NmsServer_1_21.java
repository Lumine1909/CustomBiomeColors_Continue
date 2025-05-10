package me.arthed.custombiomecolors.nms;

import me.arthed.custombiomecolors.utils.objects.BiomeColors;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
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
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

public class NmsServer_1_21 implements NmsServer {

    private final MappedRegistry<Biome> biomeRegistry = (MappedRegistry<Biome>) MinecraftServer.getServer().registryAccess().registry(Registries.BIOME).orElseThrow();

    public NmsBiome getBiomeFromBiomeKey(BiomeKey biomeKey) {
        return new NmsBiome_1_21(this.biomeRegistry.getOrThrow(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key, biomeKey.value))));
    }

    public NmsBiome getBiomeFromBiomeBase(Object biomeBase) {
        return new NmsBiome_1_21(((Holder<Biome>) biomeBase).value());
    }

    public boolean doesBiomeExist(BiomeKey biomeKey) {
        return this.biomeRegistry.containsKey(ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key, biomeKey.value)));
    }

    public void loadBiome(BiomeKey biomeKey, BiomeColors biomeColors) {
        Biome biomeBase = this.biomeRegistry.getOrThrow(ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("minecraft", "plains")
        ));

        ResourceKey<Biome> customBiomeKey = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(biomeKey.key, biomeKey.value));
        Biome.BiomeBuilder customBiomeBuilder = new Biome.BiomeBuilder()
            .generationSettings(biomeBase.getGenerationSettings())
            .mobSpawnSettings(biomeBase.getMobSettings())
            .hasPrecipitation(biomeBase.hasPrecipitation())
            .temperature(biomeBase.climateSettings.temperature())
            .downfall(biomeBase.climateSettings.downfall())
            .temperatureAdjustment(biomeBase.climateSettings.temperatureModifier());

        BiomeSpecialEffects.Builder customBiomeColors = new BiomeSpecialEffects.Builder();
        customBiomeColors.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE)
            .waterColor(biomeColors.getWaterColor())
            .waterFogColor(biomeColors.getWaterFogColor())
            .skyColor(biomeColors.getSkyColor())
            .fogColor(biomeColors.getFogColor());
        if (biomeColors.getGrassColor() != 0) {
            customBiomeColors.grassColorOverride(biomeColors.getGrassColor());
        }
        if (biomeColors.getFoliageColor() != 0) {
            customBiomeColors.foliageColorOverride(biomeColors.getFoliageColor());
        }

        customBiomeBuilder.specialEffects(customBiomeColors.build());
        Biome customBiome = customBiomeBuilder.build();
        this.registerBiome(customBiome, customBiomeKey);
    }

    public void setBlocksBiome(Block block, NmsBiome nmsBiome) {
        BlockPos blockPosition = new BlockPos(block.getX(), block.getY(), block.getZ());
        Level nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        chunk.setBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2, Holder.direct(nmsBiome.getBiomeBase()));
    }

    public Object getBlocksBiomeBase(Block block) {
        BlockPos blockPosition = new BlockPos(block.getX(), block.getY(), block.getZ());
        Level nmsWorld = ((CraftWorld) block.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        return chunk.getNoiseBiome(block.getX() >> 2, block.getY() >> 2, block.getZ() >> 2);
    }

    public void registerBiome(Object biomeBase, Object biomeMinecraftKey) {
        try {
            Field frozen = MappedRegistry.class.getDeclaredField("frozen");
            frozen.setAccessible(true);
            frozen.set(this.biomeRegistry, false);

            Field unregisteredIntrusiveHolders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            unregisteredIntrusiveHolders.setAccessible(true);
            unregisteredIntrusiveHolders.set(this.biomeRegistry, new IdentityHashMap<>());

            //biome is the BiomeBase that you're registering
            //f is createIntrusiveHolder
            this.biomeRegistry.createIntrusiveHolder((Biome) biomeBase);
            //a is RegistryMaterials.register
            this.biomeRegistry.register((ResourceKey<Biome>) biomeMinecraftKey, (Biome) biomeBase, RegistrationInfo.BUILT_IN);

            //Make unregisteredIntrusiveHolders null again to remove potential for undefined behaviour
            unregisteredIntrusiveHolders.set(this.biomeRegistry, null);

            frozen.set(this.biomeRegistry, true);
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public String getBiomeString(NmsBiome nmsBiome) {
        ResourceLocation minecraftKey = this.biomeRegistry.getKey(nmsBiome.getBiomeBase());
        if (minecraftKey != null)
            return minecraftKey.toString();
        return "minecraft:forest";
    }
}