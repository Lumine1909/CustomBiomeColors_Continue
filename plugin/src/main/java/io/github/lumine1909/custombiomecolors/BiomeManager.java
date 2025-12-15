package io.github.lumine1909.custombiomecolors;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import io.github.lumine1909.custombiomecolors.data.DataManager;
import io.github.lumine1909.custombiomecolors.nms.NmsBiome;
import io.github.lumine1909.custombiomecolors.nms.NmsServer;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import io.github.lumine1909.custombiomecolors.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("rawtypes")
public class BiomeManager {

    private final NmsServer nmsServer = CustomBiomeColors.getInstance().getNmsServer();
    private final DataManager dataManager = CustomBiomeColors.getInstance().getDataManager();

    private static BiomeType getOrCreate(String id) {
        BiomeType biomeType;
        if ((biomeType = BiomeTypes.get(id)) != null) {
            return biomeType;
        }
        biomeType = new BiomeType(id);
        BiomeTypes.register(biomeType);
        return biomeType;
    }

    public void changeBiomeColor(Player player, Region region, ColorType colorType, int color, Runnable runWhenDone) {
        this.changeBiomeColor(player, region, colorType, color, new BiomeKey("cbc", StringUtil.randomString(10)), false, runWhenDone);
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public void changeBiomeColor(Player player, Region region, ColorType colorType, int color, BiomeKey biomeKey, boolean forceKey, Runnable runWhenDone) {
        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
        World weWorld = BukkitAdapter.adapt(player.getWorld());

        FaweAPI.getTaskManager().async(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(weWorld).fastMode(true).actor(wePlayer).build()) {
                editSession.setReorderMode(EditSession.ReorderMode.FAST);
                var pos = region.getMinimumPoint();
                Location loc = new Location(player.getWorld(), pos.x(), pos.y(), pos.z());
                NmsBiome biome = nmsServer.getWrappedBiomeHolder(nmsServer.getBiomeAt(loc));
                ColorData colorData = biome.getBiomeData().colorData().clone().set(colorType, color);
                NmsBiome newBiome = dataManager.getBiomeByColorOrElse(forceKey, colorData, () -> biome.cloneWithDifferentColor(nmsServer, biomeKey, colorData));
                BiomeType type = getOrCreate(newBiome.getBiomeData().biomeKey().toString());
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