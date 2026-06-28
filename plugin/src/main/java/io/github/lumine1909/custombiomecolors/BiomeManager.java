package io.github.lumine1909.custombiomecolors;

import com.sk89q.worldedit.regions.Region;
import io.github.lumine1909.custombiomecolors.data.DataManager;
import io.github.lumine1909.custombiomecolors.nms.BiomeAccessor;
import io.github.lumine1909.custombiomecolors.nms.ServerDataHandler;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import io.github.lumine1909.custombiomecolors.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SuppressWarnings("rawtypes")
public class BiomeManager {

    private final ServerDataHandler serverDataHandler = CustomBiomeColors.getInstance().getServerDataHandler();
    private final DataManager dataManager = CustomBiomeColors.getInstance().getDataManager();

    public void changeBiomeColor(Player player, Region region, ColorType colorType, Integer color, Runnable runWhenDone) {
        this.changeBiomeColor(player, region, colorType, color, new BiomeKey("cbc", StringUtil.randomString(10)), false, runWhenDone);
    }

    @SuppressWarnings("unchecked")
    public BiomeAccessor createNewBiome(Location loc, Function<ColorData.Builder, ColorData.Builder> colorChanger, BiomeKey biomeKey, boolean forceCreateNew) {
        BiomeAccessor biome = serverDataHandler.wrapToAccessor(serverDataHandler.getBiomeAt(loc));
        ColorData colorData = colorChanger.apply(biome.getBiomeData().colorData().mutable()).build();
        return dataManager.getBiomeByColorOrElse(forceCreateNew, colorData, () -> biome.cloneWithDifferentColor(serverDataHandler, biomeKey, colorData));
    }

    public void changeBiomeColor(Player player, Region region, ColorType colorType, Integer color, BiomeKey biomeKey, boolean forceCreateNew, Runnable runWhenDone) {
        if (!TaskAccumulator.PLAYER_ACCUMULATOR_MAP.containsKey(player)) {
            player.sendMessage(Component.text("[CustomBiomeColors] Changing the biome of " + region.getVolume() + " blocks...", NamedTextColor.AQUA));
            if (region.getVolume() > 200000) {
                player.sendMessage(Component.text("[CustomBiomeColors] This might take a while.", NamedTextColor.AQUA));
            }
            CustomBiomeColors.getInstance().getWorldEditHandler().applyChange(player, region, loc -> createNewBiome(loc, builder -> builder.set(colorType, color), biomeKey, forceCreateNew), runWhenDone);
            return;
        }
        TaskAccumulator executor = TaskAccumulator.PLAYER_ACCUMULATOR_MAP.get(player);
        executor.add(biomeKey, forceCreateNew, builder -> builder.set(colorType, color), runWhenDone);
        player.sendMessage(Component.text("[CustomBiomeColors] Added a change to batch modification task", NamedTextColor.AQUA));
    }

    public static class TaskAccumulator {

        public static Map<Player, TaskAccumulator> PLAYER_ACCUMULATOR_MAP = new HashMap<>();

        private final Player player;
        private final Region region;

        private BiomeKey key = null;
        private boolean forceCreateNew = false;
        private Function<ColorData.Builder, ColorData.Builder> colorChanger = UnaryOperator.identity();
        private Runnable whenDone = null;

        public TaskAccumulator(Player player, Region region) {
            this.player = player;
            this.region = region;
        }

        public void add(BiomeKey key, boolean forceCreateNew, UnaryOperator<ColorData.Builder> colorChanger, Runnable whenDone) {
            this.key = (this.key == null || !key.key().equals("cbc")) ? key : this.key;
            this.forceCreateNew |= forceCreateNew;
            this.colorChanger = this.colorChanger.andThen(colorChanger);
            this.whenDone = whenDone;
        }

        public void commit() {
            BiomeManager manager = CustomBiomeColors.getInstance().getBiomeManager();
            CustomBiomeColors.getInstance().getWorldEditHandler().applyChange(player, region, loc -> manager.createNewBiome(loc, colorChanger, key, forceCreateNew), whenDone);
            PLAYER_ACCUMULATOR_MAP.remove(player);
        }
    }
}