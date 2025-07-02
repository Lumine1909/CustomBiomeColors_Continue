package me.arthed.custombiomecolors;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.biome.BiomeType;
import me.arthed.custombiomecolors.data.DataManager;
import me.arthed.custombiomecolors.nms.NmsBiome;
import me.arthed.custombiomecolors.nms.NmsServer;
import me.arthed.custombiomecolors.utils.StringUtil;
import me.arthed.custombiomecolors.utils.objects.BiomeColorType;
import me.arthed.custombiomecolors.utils.objects.BiomeKey;
import me.arthed.custombiomecolors.utils.objects.ColorData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
public class BiomeManager {

    private final NmsServer nmsServer = CustomBiomeColors.getInstance().getNmsServer();
    private final DataManager dataManager = CustomBiomeColors.getInstance().getDataManager();

    public void changeBiomeColor(Player player, Region region, BiomeColorType colorType, int color, boolean forceKey, Runnable runWhenDone) {
        this.changeBiomeColor(player, region, colorType, color, new BiomeKey("cbc", StringUtil.randomString(8)), forceKey, runWhenDone);
    }

    public void changeBiomeColor(Player player, Region region, BiomeColorType colorType, int color, BiomeKey biomeKey, boolean forceKey, Runnable runWhenDone) {
        Bukkit.getScheduler().runTaskAsynchronously(CustomBiomeColors.getInstance(), () -> {
            com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);

            Map<BiomeType, List<BlockVector3>> biomesForChange = new HashMap<>();

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(wePlayer)) {
                for (BlockVector3 pos : region) {
                    BlockVector3 vector3 = BlockVector3.at(pos.x(), pos.y(), pos.z());
                    BiomeType type = editSession.getBiome(vector3);
                    biomesForChange.computeIfAbsent(type, k -> new ArrayList<>()).add(vector3);
                }

                int num = 0;
                for (var entry : biomesForChange.entrySet()) {
                    BiomeKey individualKey = biomeKey.createSuffix("_" + num++);
                    NmsBiome biome = nmsServer.getBiomeFromBiomeKey(BiomeKey.fromString(entry.getKey().id()));
                    ColorData colorData = biome.getBiomeData().colorData().setColor(colorType, color);
                    NmsBiome newBiome = dataManager.getBiomeByColorOrElse(forceKey, colorData, () -> biome.cloneWithDifferentColor(nmsServer, individualKey, colorData));
                    for (var vec3 : entry.getValue()) {
                        nmsServer.setBiomeAt(new Location(player.getWorld(), vec3.x(), vec3.y(), vec3.z()), newBiome);
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