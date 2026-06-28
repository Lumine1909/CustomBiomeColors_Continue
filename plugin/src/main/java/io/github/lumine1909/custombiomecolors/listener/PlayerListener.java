package io.github.lumine1909.custombiomecolors.listener;

import io.github.lumine1909.custombiomecolors.BiomeManager;
import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        CustomBiomeColors.getInstance().getPacketHandler().injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        BiomeManager.TaskAccumulator.PLAYER_ACCUMULATOR_MAP.remove(e.getPlayer());
    }
}
