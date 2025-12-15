package io.github.lumine1909.custombiomecolors.command;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.nms.NmsBiome;
import io.github.lumine1909.custombiomecolors.nms.NmsServer;
import io.github.lumine1909.custombiomecolors.object.BiomeData;
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

    private static final NmsServer nmsServer = CustomBiomeColors.getInstance().getNmsServer();

    public GetBiomeColorsCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("/getbiomecolors")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("/getbiomecolors")).setTabCompleter(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        NmsBiome biome = nmsServer.getWrappedBiomeHolder(nmsServer.getBiomeAt(player.getLocation()));
        BiomeData biomeData = biome.getBiomeData();
        player.sendMessage(Component.text("Colors of the biome you are in (" + biomeData.biomeKey() + "): ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
        biomeData.colorData().forEach((colorType, color) -> {
            if (!colorType.isSupported()) {
                return;
            }
            if (colorType.isSpecial()) {
                player.sendMessage(Component.text(" - " + colorType.messageName() + ": ", NamedTextColor.GRAY).append(MessageUtil.getColorMessageSpecial(color, colorType, biome.getTemperature(), biome.getHumidity()).decorate(TextDecoration.BOLD)));
            } else {
                player.sendMessage(Component.text(" - " + colorType.messageName() + ": ", NamedTextColor.GRAY).append(MessageUtil.getColorMessage(color).decorate(TextDecoration.BOLD)));
            }
        });
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return Collections.emptyList();
    }
}