package io.github.lumine1909.custombiomecolors.command;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReloadCommand implements TabExecutor {

    public ReloadCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("cbc")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("cbc")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            return true;
        }
        CustomBiomeColors.getInstance().callReload(sender);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length <= 1) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }
}