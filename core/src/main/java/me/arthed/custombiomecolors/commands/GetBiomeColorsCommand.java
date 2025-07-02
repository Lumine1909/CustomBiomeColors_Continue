package me.arthed.custombiomecolors.commands;

import me.arthed.custombiomecolors.CustomBiomeColors;
import me.arthed.custombiomecolors.nms.NmsBiome;
import me.arthed.custombiomecolors.nms.NmsServer;
import me.arthed.custombiomecolors.utils.MessageUtil;
import me.arthed.custombiomecolors.utils.objects.BiomeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class GetBiomeColorsCommand implements CommandExecutor {

    private static final NmsServer nmsServer = CustomBiomeColors.getInstance().getNmsServer();

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("/getbiomecolors")) {
            if (sender instanceof Player player) {
                NmsBiome biome = nmsServer.getWrappedBiomeHolder(nmsServer.getBiomeAt(player.getLocation()));
                BiomeData biomeData = biome.getBiomeData();
                player.sendMessage(Component.text("Colors of the biome you are in (" + biomeData.biomeKey() + "): ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
                player.sendMessage(Component.text(" - Grass: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessageGrass(biomeData.colorData().grassColor(), biome.getTemperature(), biome.getHumidity()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Foliage: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessageGrass(biomeData.colorData().foliageColor(), biome.getTemperature(), biome.getHumidity()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Dry Foliage: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessageDryFoliage(biomeData.colorData().dryFoliageColor(), biome.getTemperature(), biome.getHumidity()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Water: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessage(biomeData.colorData().waterColor()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Water Fog: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessage(biomeData.colorData().waterFogColor()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Sky: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessage(biomeData.colorData().skyColor()).decorate(TextDecoration.BOLD)));
                player.sendMessage(Component.text(" - Fog: ", NamedTextColor.GRAY).append(MessageUtil.getColorMessage(biomeData.colorData().fogColor()).decorate(TextDecoration.BOLD)));
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
        }

        return false;
    }
}