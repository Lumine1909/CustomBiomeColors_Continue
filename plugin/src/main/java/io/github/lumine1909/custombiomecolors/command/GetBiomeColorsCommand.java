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

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String @NotNull [] args) {
        if (cmd.getName().equalsIgnoreCase("/getbiomecolors")) {
            if (sender instanceof Player player) {
                NmsBiome biome = nmsServer.getWrappedBiomeHolder(nmsServer.getBiomeAt(player.getLocation()));
                BiomeData biomeData = biome.getBiomeData();
                player.sendMessage(Component.text("Colors of the biome you are in (" + biomeData.biomeKey() + "): ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                player.sendMessage(Component.text(" - Grass: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessageGrass(biomeData.colorData().get(ColorType.GRASS), biome.getTemperature(), biome.getHumidity()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Foliage: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessageFoliage(biomeData.colorData().get(ColorType.FOLIAGE), biome.getTemperature(), biome.getHumidity()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Dry Foliage: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessageDryFoliage(biomeData.colorData().get(ColorType.DRY_FOLIAGE), biome.getTemperature(), biome.getHumidity()).decorate(TextDecoration.BOLD)));

                //TODO: Support dimension_type based default color, make it looks better
                biomeData.colorData().forEach((colorType, color) -> {
                    if (!colorType.isSupported()) {
                        return;
                    }
                    if (colorType != ColorType.GRASS && colorType != ColorType.FOLIAGE && colorType != ColorType.DRY_FOLIAGE) {
                        player.sendMessage(Component.text(" - " + colorType.messageName() + ": ", NamedTextColor.GRAY).append(MessageUtil.getColorMessage(color).decorate(TextDecoration.BOLD)));
                    }
                });
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return Collections.emptyList();
    }
}