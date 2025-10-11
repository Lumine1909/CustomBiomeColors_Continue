package io.github.lumine1909.custombiomecolors.command;

import com.sk89q.worldedit.regions.Region;
import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.integration.WorldEditHandler;
import io.github.lumine1909.custombiomecolors.nms.NmsServer;
import io.github.lumine1909.custombiomecolors.util.object.BiomeColorType;
import io.github.lumine1909.custombiomecolors.util.object.BiomeKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"rawtypes"})
public class SetBiomeColorCommand implements TabExecutor {

    private static final WorldEditHandler worldEditHandler = CustomBiomeColors.getInstance().getWorldEditHandler();
    private static final NmsServer nmsServer = CustomBiomeColors.getInstance().getNmsServer();

    private final String command;
    private final BiomeColorType colorType;

    public SetBiomeColorCommand(String command, BiomeColorType colorType) {
        this.command = command;
        this.colorType = colorType;
    }

    public static void register(@Nullable PluginCommand command, String subCommand, BiomeColorType type) {
        if (command == null) {
            throw new RuntimeException("Unable to register command, command is null");
        }
        SetBiomeColorCommand setBiomeColorCommand = new SetBiomeColorCommand(subCommand, type);
        command.setExecutor(setBiomeColorCommand);
        command.setTabCompleter(setBiomeColorCommand);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("[CustomBiomeColors] Only players can use this command!", NamedTextColor.RED));
            return true;
        }
        if (!cmd.getName().equalsIgnoreCase(this.command)) {
            return true;
        }
        if (args.length > 0) {
            Optional<Region> optionalRegion = worldEditHandler.getSelectedRegion(sender.getName());
            if (optionalRegion.isEmpty()) {
                sender.sendMessage(Component.text("[CustomBiomeColors] Make a region selection first!", NamedTextColor.RED));
                return true;
            }

            Region selectedRegion = optionalRegion.get();
            int color;
            try {
                color = Integer.parseInt(args[0].replace("#", ""), 16);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("[CustomBiomeColors] Invalid color, please use a valid hex color code!", NamedTextColor.RED));
                return true;
            }

            long time = System.currentTimeMillis();
            Runnable runWhenDone = () -> sender.sendMessage(Component.text("[CustomBiomeColors] Biome color was changed for approximately " + selectedRegion.getVolume() + " blocks. (" + (System.currentTimeMillis() - time) / 1000.0f + "s)", NamedTextColor.GREEN));

            if (args.length > 1) {
                if (!args[1].contains(":")) {
                    sender.sendMessage(Component.text("[CustomBiomeColors] The biome name must contain a colon! ( : )", NamedTextColor.RED));
                    return true;
                }
                BiomeKey biomeKey = BiomeKey.fromString(args[1]);
                if (nmsServer.doesBiomeExist(biomeKey)) {
                    sender.sendMessage(Component.text("[CustomBiomeColors] There is already exists a biome with that name, please use another one!", NamedTextColor.RED));
                    return true;
                }

                CustomBiomeColors.getInstance().getBiomeManager().changeBiomeColor(player, selectedRegion, this.colorType, color, biomeKey, true, runWhenDone);
            } else {
                CustomBiomeColors.getInstance().getBiomeManager().changeBiomeColor(player, selectedRegion, this.colorType, color, runWhenDone);
            }

            sender.sendMessage(Component.text("[CustomBiomeColors] Changing the biome of " + optionalRegion.orElseThrow().getVolume() + " blocks...", NamedTextColor.AQUA));
            if (optionalRegion.orElseThrow().getVolume() > 200000) {
                sender.sendMessage(Component.text("[CustomBiomeColors] This might take a while.", NamedTextColor.AQUA));
            }
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return List.of("#HEXCODE");
        } else if (args.length == 2) {
            return List.of("biome:name");
        }
        return Collections.emptyList();
    }
}
