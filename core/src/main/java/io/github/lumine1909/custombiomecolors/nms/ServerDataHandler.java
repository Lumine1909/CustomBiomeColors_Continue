package io.github.lumine1909.custombiomecolors.nms;

import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.function.Function;

import static io.github.lumine1909.custombiomecolors.util.Reflection.*;

public interface ServerDataHandler<Biome, Holder, ResourceKey> extends ExtraDataHandler {

    BiomeAccessor<Biome, Holder, ResourceKey> getBiomeFromKey(BiomeKey biomeKey);

    BiomeAccessor<Biome, Holder, ResourceKey> wrapToAccessor(Holder biomeBase);

    boolean hasBiome(BiomeKey biomeKey);

    BiomeAccessor<Biome, Holder, ResourceKey> createCustomBiome(BiomeData biomeData);

    BiomeSpecialEffects buildSpecialEffects(ColorData colorData);

    BiomeAccessor<Biome, Holder, ResourceKey> rewrapAccessor(Holder biomeBase, BiomeData data);

    default void applyAttributeColors(Biome biome, ColorData colorData) {
    }

    /**
     * Recolors an already-registered custom biome in place instead of registering a new
     * biome under a new id. Keeps the same Holder/registry entry so blocks already using
     * this biome, and players who join after this call, see the updated colors without the
     * plugin needing to mint a new dynamic biome (which would need a client rejoin to sync
     * and would otherwise make the plugin fall back to vanilla Plains for the current session,
     * see PacketHandler#getModifiedId).
     */
    @SuppressWarnings("unchecked")
    default BiomeAccessor<Biome, Holder, ResourceKey> updateBiomeColor(BiomeKey biomeKey, Function<ColorData.Builder, ColorData.Builder> colorChanger) {
        BiomeAccessor<Biome, Holder, ResourceKey> accessor = getBiomeFromKey(biomeKey);
        ColorData newColorData = colorChanger.apply(accessor.getBiomeData().colorData().mutable()).build();
        Biome biome = accessor.getBiome();

        field$Biome$specialEffects.set(biome, buildSpecialEffects(newColorData));
        applyAttributeColors(biome, newColorData);

        BiomeData newData = new BiomeData(biomeKey, accessor.getBiomeData().baseBiomeKey(), newColorData);
        return rewrapAccessor(accessor.getBiomeHolder(), newData);
    }

    @SuppressWarnings("unchecked")
    default void setBiomeAt(Location location, BiomeAccessor<Biome, Holder, ResourceKey> biomeAccessor) {
        BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();

        net.minecraft.world.level.chunk.LevelChunk chunk = nmsWorld.getChunkAt(blockPosition);
        chunk.setBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2, (net.minecraft.core.Holder<net.minecraft.world.level.biome.Biome>) biomeAccessor.getBiomeHolder());
    }

    @SuppressWarnings("unchecked")
    default Holder getBiomeAt(Location location) {
        Level nmsWorld = ((CraftWorld) location.getWorld()).getHandle();
        return (Holder) nmsWorld.getNoiseBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2);
    }

    MappedRegistry<Biome> getRegistry();

    Collection<?> getTagList(Holder original);

    @SuppressWarnings("unchecked")
    default Holder registerBiome(Holder original, Biome biome, ResourceKey resourceKey) {
        try {
            field$MappedRegistry$frozen.set(getRegistry(), false);
            field$MappedRegistry$unregisteredIntrusiveHolders.set(getRegistry(), new IdentityHashMap<>());

            getRegistry().createIntrusiveHolder(biome);
            Holder holder = (Holder) getRegistry().register((net.minecraft.resources.ResourceKey<Biome>) resourceKey, biome, RegistrationInfo.BUILT_IN);
            method$Holder$bindTags.invoke(holder, getTagList(original));

            field$MappedRegistry$unregisteredIntrusiveHolders.set(getRegistry(), null);
            field$MappedRegistry$frozen.set(getRegistry(), true);

            return holder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}