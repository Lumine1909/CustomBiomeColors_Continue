package io.github.lumine1909.custombiomecolors.listener;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        CustomBiomeColors.getInstance().getServerDataHandler().modifyEnvironmentSampler(e.getWorld());
    }
}
