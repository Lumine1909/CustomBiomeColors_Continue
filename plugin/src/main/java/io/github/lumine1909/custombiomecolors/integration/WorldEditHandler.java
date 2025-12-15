package io.github.lumine1909.custombiomecolors.integration;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WorldEditHandler {

    private final WorldEdit worldEdit = WorldEdit.getInstance();

    @Nullable
    public Region getSelectedRegion(String authorsName) {
        LocalSession worldEditSession = worldEdit.getSessionManager().findByName(authorsName);
        if (worldEditSession == null || worldEditSession.getSelectionWorld() == null) {
            return null;
        }
        RegionSelector regionSelector = worldEditSession.getRegionSelector(worldEditSession.getSelectionWorld());
        if (!regionSelector.isDefined()) {
            return null;
        }
        try {
            return regionSelector.getRegion();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}