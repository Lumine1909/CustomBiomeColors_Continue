package io.github.lumine1909.custombiomecolors;

import com.sk89q.worldedit.regions.Region;
import io.github.lumine1909.custombiomecolors.data.DataManager;
import io.github.lumine1909.custombiomecolors.nms.BiomeAccessor;
import io.github.lumine1909.custombiomecolors.nms.ServerDataHandler;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import io.github.lumine1909.custombiomecolors.util.StringUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("rawtypes")
public class BiomeManager {

    private final ServerDataHandler serverDataHandler = CustomBiomeColors.getInstance().getServerDataHandler();
    private final DataManager dataManager = CustomBiomeColors.getInstance().getDataManager();

    public void changeBiomeColor(Player player, Region region, ColorType colorType, Integer color, Runnable runWhenDone) {
        this.changeBiomeColor(player, region, colorType, color, new BiomeKey("cbc", StringUtil.randomString(10)), false, runWhenDone);
    }

    @SuppressWarnings("unchecked")
    public BiomeAccessor createNewBiome(Location loc, ColorType colorType, Integer color, BiomeKey biomeKey, boolean forceCreateNew) {
        BiomeAccessor biome = serverDataHandler.wrapToAccessor(serverDataHandler.getBiomeAt(loc));
        ColorData colorData = biome.getBiomeData().colorData().mutable().set(colorType, color).build();
        return dataManager.getBiomeByColorOrElse(forceCreateNew, colorData, () -> biome.cloneWithDifferentColor(serverDataHandler, biomeKey, colorData));
    }

    public void changeBiomeColor(Player player, Region region, ColorType colorType, Integer color, BiomeKey biomeKey, boolean forceCreateNew, Runnable runWhenDone) {
        CustomBiomeColors.getInstance().getWorldEditHandler().applyChange(player, region, loc -> createNewBiome(loc, colorType, color, biomeKey, forceCreateNew), runWhenDone);
    }
}