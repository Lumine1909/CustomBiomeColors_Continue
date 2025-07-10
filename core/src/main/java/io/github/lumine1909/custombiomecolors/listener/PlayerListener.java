package io.github.lumine1909.custombiomecolors.listener;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskLater(CustomBiomeColors.getInstance(), () -> CustomBiomeColors.getInstance().getPacketHandler().injectPlayer(e.getPlayer()), 1L);
    }
}
