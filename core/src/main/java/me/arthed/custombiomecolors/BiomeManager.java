package me.arthed.custombiomecolors;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.FlatRegionFunction;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.biome.BiomeReplace;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.visitor.FlatRegionVisitor;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.regions.FlatRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import me.arthed.custombiomecolors.data.DataManager;
import me.arthed.custombiomecolors.nms.NmsBiome;
import me.arthed.custombiomecolors.nms.NmsServer;
import me.arthed.custombiomecolors.utils.StringUtil;
import me.arthed.custombiomecolors.utils.objects.BiomeColorType;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import me.arthed.custombiomecolors.utils.objects.ColorData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings({"rawtypes", "unchecked"})
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

    public void changeBiomeColor(Player player, Region region, BiomeColorType colorType, int color, boolean forceKey, Runnable runWhenDone) {
        this.changeBiomeColor(player, region, colorType, color, new BiomeKey("cbc", StringUtil.randomString(8)), forceKey, runWhenDone);
    }

    public void changeBiomeColor(Player player, Region region, BiomeColorType colorType, int color, BiomeKey biomeKey, boolean forceKey, Runnable runWhenDone) {
        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
        World weWorld = BukkitAdapter.adapt(player.getWorld());

        FaweAPI.getTaskManager().async(() -> {
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(weWorld).fastMode(true).actor(wePlayer).build()) {
                editSession.setReorderMode(EditSession.ReorderMode.FAST);
                if (CustomBiomeColors.fastEditorMode) {
                    var pos = region.getMinimumPoint();
                    Location loc = new Location(player.getWorld(), pos.x(), pos.y(), pos.z());
                    NmsBiome biome = nmsServer.getWrappedBiomeHolder(nmsServer.getBiomeAt(loc));
                    ColorData colorData = biome.getBiomeData().colorData().setColor(colorType, color);
                    NmsBiome newBiome = dataManager.getBiomeByColorOrElse(forceKey, colorData, () -> biome.cloneWithDifferentColor(nmsServer, biomeKey.createSuffix("_0"), colorData));
                    BiomeType type = getOrCreate(newBiome.getBiomeData().biomeKey().toString());
                    if (region instanceof FlatRegion flatRegion) {
                        FlatRegionFunction replace = new BiomeReplace(editSession, type);
                        FlatRegionVisitor visitor = new FlatRegionVisitor(flatRegion, replace);
                        Operations.completeLegacy(visitor);
                    } else {
                        RegionFunction replace = new BiomeReplace(editSession, type);
                        RegionVisitor visitor = new RegionVisitor(region, replace);
                        Operations.completeLegacy(visitor);
                    }
                } else {
                    int[] num = new int[]{0};
                    for (var pos : region) {
                        Location loc = new Location(player.getWorld(), pos.x(), pos.y(), pos.z());
                        NmsBiome biome = nmsServer.getWrappedBiomeHolder(nmsServer.getBiomeAt(loc));
                        ColorData colorData = biome.getBiomeData().colorData().setColor(colorType, color);
                        NmsBiome newBiome = dataManager.getBiomeByColorOrElse(forceKey, colorData, () -> biome.cloneWithDifferentColor(nmsServer, biomeKey.createSuffix("_" + num[0]++), colorData));
                        nmsServer.setBiomeAt(loc, newBiome);
                    }
                }
                editSession.flushQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            runWhenDone.run();
        });
    }
}