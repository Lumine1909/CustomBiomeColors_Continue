package io.github.lumine1909.custombiomecolors.integration;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WorldEditHandler {

    private final WorldEdit worldEdit = WorldEdit.getInstance();

    @NotNull
    public Optional<Region> getSelectedRegion(String authorsName) {
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(authorsName);
        if (worldEditSession == null) {
            return Optional.empty();
        }
        if (worldEditSession.getSelectionWorld() == null) {
            return Optional.empty();
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (regionSelector.isDefined()) {
            try {
                Region region = regionSelector.getRegion();
                return Optional.of(region);
            } catch (IncompleteRegionException e) {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }
}