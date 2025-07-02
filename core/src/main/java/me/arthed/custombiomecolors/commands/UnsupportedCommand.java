package me.arthed.custombiomecolors.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UnsupportedCommand implements CommandExecutor {

    private final String supportFrom;

    public UnsupportedCommand(String supportFrom) {
        this.supportFrom = supportFrom;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        sender.sendMessage(Component.text("This command used a unsupported feature that introduced in version " + this.supportFrom + ", which is currently unavailable!", NamedTextColor.RED));
        return true;
    }
}
