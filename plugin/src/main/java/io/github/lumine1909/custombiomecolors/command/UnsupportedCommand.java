package io.github.lumine1909.custombiomecolors.command;

import io.github.lumine1909.custombiomecolors.object.ColorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class UnsupportedCommand implements TabExecutor {

    private final String supportSince;

    public UnsupportedCommand(String supportSince) {
        this.supportSince = supportSince;
    }

    public static void register(@Nullable PluginCommand command, ColorType colorType) {
        if (command == null) {
            throw new RuntimeException("Unable to register command, command is null");
        }
        UnsupportedCommand unsupportedCommand = new UnsupportedCommand(colorType.getSupportSince());
        command.setExecutor(unsupportedCommand);
        command.setTabCompleter(unsupportedCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        sender.sendMessage(Component.text("This command uses a feature introduced in version " + this.supportSince + ", which is currently unavailable!", NamedTextColor.RED));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return Collections.emptyList();
    }
}
