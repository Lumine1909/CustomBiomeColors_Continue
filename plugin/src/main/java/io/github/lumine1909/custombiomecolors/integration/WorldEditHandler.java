package io.github.lumine1909.custombiomecolors.integration;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import io.github.lumine1909.custombiomecolors.nms.BiomeAccessor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public class WorldEditHandler {

    private final WorldEdit worldEdit = WorldEdit.getInstance();
    private final Consumer<Runnable> taskExecutor = getExecutor();

    private static Consumer<Runnable> getExecutor() {
        try {
            Class.forName("com.fastasyncworldedit.core.FaweAPI");
            // Fawe exists, great!
            return runnable -> FaweAPI.getTaskManager().async(runnable);
        } catch (ClassNotFoundException e) {
            // Fawe does not exist, sync WE call
            return Runnable::run;
        }
    }

    public static BiomeType getOrCreate(String id) {
        BiomeType biomeType;
        if ((biomeType = BiomeTypes.get(id)) != null) {
            return biomeType;
        }
        biomeType = new BiomeType(id);
        BiomeTypes.register(biomeType);
        return biomeType;
    }

    public @Nullable Region getSelectedRegion(String authorsName) {
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(authorsName);
        if (worldEditSession == null || worldEditSession.getSelectionWorld() == null) {
            return null;
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (!regionSelector.isDefined()) {
            return null;
        }
        try {
            return regionSelector.getRegion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"deprecation", "rawtypes"})
    public void applyChange(Player player, Region region, Function<Location, BiomeAccessor> biomeGetter, Runnable runWhenDone) {
        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
        World weWorld = BukkitAdapter.adapt(player.getWorld());

        taskExecutor.accept(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(weWorld).fastMode(true).actor(wePlayer).build()) {
                editSession.setReorderMode(EditSession.ReorderMode.FAST);
                BlockVector3 pos = region.getMinimumPoint();
                Location loc = new Location(player.getWorld(), pos.x(), pos.y(), pos.z());
                BiomeType type = getOrCreate(biomeGetter.apply(loc).getBiomeData().biomeKey().toString());
                RegionFunction replace = new BiomeReplace(editSession, type);
                RegionVisitor visitor = new RegionVisitor(region, replace);
                Operations.completeLegacy(visitor);
                editSession.flushQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            runWhenDone.run();
        });
    }
}