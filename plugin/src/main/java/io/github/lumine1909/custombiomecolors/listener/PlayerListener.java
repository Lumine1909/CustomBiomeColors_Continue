package io.github.lumine1909.custombiomecolors.listener;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.utils.FoliaUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        if (FoliaUtil.isFoliaServer()) {
            Bukkit.getGlobalRegionScheduler().runDelayed(
                    CustomBiomeColors.getInstance(),
                    task -> injectPlayer(player),
                    10L
            );
        } else Bukkit.getScheduler().runTaskLater(CustomBiomeColors.getInstance(), () -> injectPlayer(e.getPlayer()), 10L);
    }

    public void injectPlayer(Player player) {
        CustomBiomeColors.getInstance().getPacketHandler().injectPlayer(player);
    }
}
