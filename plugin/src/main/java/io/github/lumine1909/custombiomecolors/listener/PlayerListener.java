package io.github.lumine1909.custombiomecolors.listener;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.util.FoliaUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        FoliaUtil.runDelayed(CustomBiomeColors.getInstance(), () -> injectPlayer(player), 10L);
    }

    public void injectPlayer(Player player) {
        CustomBiomeColors.getInstance().getPacketHandler().injectPlayer(player);
    }
}
