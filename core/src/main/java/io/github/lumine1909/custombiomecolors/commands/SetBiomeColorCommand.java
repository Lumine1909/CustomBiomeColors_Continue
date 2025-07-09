package io.github.lumine1909.custombiomecolors.commands;

import com.sk89q.worldedit.regions.Region;
import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.integration.WorldEditHandler;
import io.github.lumine1909.custombiomecolors.nms.NmsServer;
import io.github.lumine1909.custombiomecolors.utils.objects.BiomeColorType;
import io.github.lumine1909.custombiomecolors.utils.objects.BiomeKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"rawtypes", "deprecation"})
public class SetBiomeColorCommand implements TabExecutor {

    private static final WorldEditHandler worldEditHandler = CustomBiomeColors.getInstance().getWorldEditHandler();
    private static final NmsServer nmsServer = CustomBiomeColors.getInstance().getNmsServer();

    private final String command;
    private final BiomeColorType colorType;

    public SetBiomeColorCommand(String command, BiomeColorType colorType) {
        this.command = command;
        this.colorType = colorType;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }
        if (cmd.getName().equalsIgnoreCase(this.command)) {
            if (args.length > 0) {
                Optional<Region> optionalRegion = worldEditHandler.getSelectedRegion(sender.getName());
                if (optionalRegion.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "Make a region selection first.");
                    return true;
                }

                Region selectedRegion = optionalRegion.get();
                int color;
                try {
                    color = Integer.parseInt(args[0].replace("#", ""), 16);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid color. Please use a valid hex color code.");
                    return true;
                }

                long time = System.currentTimeMillis();
                Runnable runWhenDone = () -> {
                    sender.sendMessage(ChatColor.GREEN + "Biome color was changed for approximately " + selectedRegion.getVolume() + " blocks. (" + (System.currentTimeMillis() - time) / 1000.0f + "s)");
                    sender.sendMessage(ChatColor.GREEN + "You must re-join to see the changes.");
                };

                if (args.length > 1) {
                    if (!args[1].contains(":")) {
                        sender.sendMessage(ChatColor.RED + "The biome name must contain a colon. ( : )");
                        return true;
                    }
                    BiomeKey biomeKey = BiomeKey.fromString(args[1]);
                    if (nmsServer.doesBiomeExist(biomeKey)) {
                        sender.sendMessage(ChatColor.RED + "There already exists a biome with that name. Please use another one");
                        return true;
                    }

                    CustomBiomeColors.getInstance().getBiomeManager().changeBiomeColor(player, selectedRegion, this.colorType, color, true, runWhenDone);
                } else {
                    CustomBiomeColors.getInstance().getBiomeManager().changeBiomeColor(player, selectedRegion, this.colorType, color, false, runWhenDone);
                }

                sender.sendMessage(Component.text("Changing the biome of " + optionalRegion.orElseThrow().getVolume() + " blocks...", NamedTextColor.AQUA));
                if (optionalRegion.orElseThrow().getVolume() > 200000)
                    sender.sendMessage(Component.text("This might take a while.", NamedTextColor.AQUA));
                return true;
            }
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Collections.singletonList("#HEXCODE");
        } else if (args.length == 2) {
            return Collections.singletonList("biome:name");
        }
        return null;
    }
}
