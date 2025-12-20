package io.github.lumine1909.custombiomecolors.command;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.nms.BiomeAccessor;
import io.github.lumine1909.custombiomecolors.nms.ServerDataHandler;
import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import io.github.lumine1909.custombiomecolors.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class GetBiomeColorsCommand implements TabExecutor {

    private static final ServerDataHandler BIOME_DATA_HANDLER = CustomBiomeColors.getInstance().getServerDataHandler();

    public GetBiomeColorsCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("/getbiomecolors")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("/getbiomecolors")).setTabCompleter(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        BiomeAccessor biome = BIOME_DATA_HANDLER.wrapToAccessor(BIOME_DATA_HANDLER.getBiomeAt(player.getLocation()));
        BiomeData biomeData = biome.getBiomeData();
        player.sendMessage(Component.text("Colors of the biome you are in (" + biomeData.biomeKey() + "): ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        ColorData biomeColor = biomeData.colorData();
        ColorData dimensionColor = CustomBiomeColors.getInstance().getServerDataHandler().getDimensionColor(player.getLocation());
        for (ColorType colorType : ColorType.values()) {
            Component message = Component.text(" - " + colorType.messageName() + ": ", NamedTextColor.GRAY);
            boolean shouldSend = false;
            Integer color;
            if (biomeColor != null && (color = biomeColor.get(colorType)) != null) {
                message = message.append(
                        MessageUtil.getColorMessage(colorType, color, biome.getTemperature(), biome.getHumidity())
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(" (Biome)    ", NamedTextColor.GRAY));
                shouldSend = true;
            }
            if (dimensionColor != null && (color = dimensionColor.get(colorType)) != null) {
                message = message.append(
                        MessageUtil.getColorMessage(colorType, color, 0, 0)
                            .decorate(TextDecoration.BOLD))
                    .append(Component.text(" (Dimension)    ", NamedTextColor.GRAY));
                shouldSend = true;
            }
            if (shouldSend) {
                player.sendMessage(message);
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return Collections.emptyList();
    }
}