package io.github.lumine1909.custombiomecolors.command;

import com.sk89q.worldedit.regions.Region;
import io.github.lumine1909.custombiomecolors.BiomeManager;
import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.integration.WorldEditHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BatchSetBiomeColorCommand implements TabExecutor {

    private static final WorldEditHandler worldEditHandler = CustomBiomeColors.getInstance().getWorldEditHandler();

    public BatchSetBiomeColorCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("/batchsetbiomecolors")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("/batchsetbiomecolors")).setTabCompleter(this);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command.").color(NamedTextColor.RED));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Component.text("[CustomBiomeColors] You can use \"//batchsetbiomecolors start\" to create a batch modification task", NamedTextColor.YELLOW));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "start" -> {
                BiomeManager.TaskAccumulator executor = BiomeManager.TaskAccumulator.PLAYER_ACCUMULATOR_MAP.get(player);
                if (executor != null) {
                    sender.sendMessage(Component.text("[CustomBiomeColors] There is a pending batch modification task, you have to abort it first!", NamedTextColor.RED));
                    return true;
                }
                Region selectedRegion = worldEditHandler.getSelectedRegion(sender.getName());
                if (selectedRegion == null) {
                    sender.sendMessage(Component.text("[CustomBiomeColors] Make a region selection first!", NamedTextColor.RED));
                    return true;
                }
                executor = new BiomeManager.TaskAccumulator(player, selectedRegion);
                BiomeManager.TaskAccumulator.PLAYER_ACCUMULATOR_MAP.put(player, executor);
                sender.sendMessage(Component.text("[CustomBiomeColors] Started a new batch modification task", NamedTextColor.GREEN));
            }
            case "commit" -> {
                BiomeManager.TaskAccumulator executor = BiomeManager.TaskAccumulator.PLAYER_ACCUMULATOR_MAP.get(player);
                if (executor == null) {
                    sender.sendMessage(Component.text("[CustomBiomeColors] There is no pending batch modification task to commit, use \"//batchsetbiomecolors start\" to create one!", NamedTextColor.RED));
                    return true;
                }
                executor.commit();
            }
            case "abort" -> {
                if (BiomeManager.TaskAccumulator.PLAYER_ACCUMULATOR_MAP.remove(player) != null) {
                    sender.sendMessage(Component.text("[CustomBiomeColors] Aborted the pending batch modification", NamedTextColor.GREEN));
                } else {
                    sender.sendMessage(Component.text("[CustomBiomeColors] There is no pending batch modification to abort!", NamedTextColor.RED));
                }
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return args.length < 2 ? List.of("start", "commit", "abort") : Collections.emptyList();
    }
}
