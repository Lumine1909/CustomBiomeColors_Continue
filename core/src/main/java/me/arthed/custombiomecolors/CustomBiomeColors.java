package me.arthed.custombiomecolors;

import me.arthed.custombiomecolors.commands.GetBiomeColorsCommand;
import me.arthed.custombiomecolors.commands.SetBiomeColorCommand;
import me.arthed.custombiomecolors.data.DataManager;
import me.arthed.custombiomecolors.integration.WorldEditHandler;
import me.arthed.custombiomecolors.nms.NmsServer;
import me.arthed.custombiomecolors.nms.NmsServer_1_20_5;
import me.arthed.custombiomecolors.nms.NmsServer_1_21;
import me.arthed.custombiomecolors.nms.NmsServer_1_21_3;
import me.arthed.custombiomecolors.utils.BStats;
import me.arthed.custombiomecolors.utils.UpdateChecker;
import me.arthed.custombiomecolors.utils.objects.BiomeColorType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("rawtypes")
public final class CustomBiomeColors extends JavaPlugin {

    private static CustomBiomeColors instance;
    private NmsServer nmsServer;
    private BiomeManager biomeManager;
    private WorldEditHandler worldEditHandler;
    private DataManager dataManager;

    public static CustomBiomeColors getInstance() {
        return instance;
    }

    public static int obtainVersion() {
        try {
            String[] versions = Bukkit.getMinecraftVersion().split("\\.");
            if (versions.length == 2) {
                return Integer.parseInt(versions[1]) * 100;
            } else if (versions.length == 3) {
                return Integer.parseInt(versions[1]) * 100 + Integer.parseInt(versions[2]);
            }
        } catch (Exception ignored) {
        }
        return -1;
    }

    @NotNull
    public NmsServer getNmsServer() {
        return this.nmsServer;
    }

    @NotNull
    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    public WorldEditHandler getWorldEditHandler() {
        return this.worldEditHandler;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    @Override
    public void onLoad() {
        instance = this;

        int version = obtainVersion();
        if (version >= 2103) {
            nmsServer = new NmsServer_1_21_3();
        } else if (version >= 2100) {
            nmsServer = new NmsServer_1_21();
        } else if (version >= 2005) {
            nmsServer = new NmsServer_1_20_5();
        } else {
            throw new IllegalStateException("This plugin only support MC 1.20.5 - 1.21.5, for other versions, please contact author at https://github.com/Lumine1909/CustomBiomeColors_Continue/issues");
        }

        this.dataManager = new DataManager("data.json");
        this.dataManager.loadBiomes();
    }

    @Override
    public void onEnable() {
        new BStats(this, 12660);

        this.biomeManager = new BiomeManager();
        this.worldEditHandler = new WorldEditHandler();

        Objects.requireNonNull(this.getCommand("/setgrasscolor")).setExecutor(new SetBiomeColorCommand("/setgrasscolor", BiomeColorType.GRASS));
        Objects.requireNonNull(this.getCommand("/setfoliagecolor")).setExecutor(new SetBiomeColorCommand("/setfoliagecolor", BiomeColorType.FOLIAGE));
        Objects.requireNonNull(this.getCommand("/setwatercolor")).setExecutor(new SetBiomeColorCommand("/setwatercolor", BiomeColorType.WATER));
        Objects.requireNonNull(this.getCommand("/setwaterfogcolor")).setExecutor(new SetBiomeColorCommand("/setwaterfogcolor", BiomeColorType.WATER_FOG));
        Objects.requireNonNull(this.getCommand("/setskycolor")).setExecutor(new SetBiomeColorCommand("/setskycolor", BiomeColorType.SKY));
        Objects.requireNonNull(this.getCommand("/setfogcolor")).setExecutor(new SetBiomeColorCommand("/setfogcolor", BiomeColorType.FOG));
        Objects.requireNonNull(this.getCommand("/getbiomecolors")).setExecutor(new GetBiomeColorsCommand());

        new UpdateChecker(this);
    }

    @Override
    public void onDisable() {
        try {
            this.dataManager.save();
        } catch (IOException ignore) {
        }
    }
}