package io.github.lumine1909.custombiomecolors;

import io.github.lumine1909.custombiomecolors.command.GetBiomeColorsCommand;
import io.github.lumine1909.custombiomecolors.command.ReloadCommand;
import io.github.lumine1909.custombiomecolors.command.SetBiomeColorCommand;
import io.github.lumine1909.custombiomecolors.command.UnsupportedCommand;
import io.github.lumine1909.custombiomecolors.data.DataManager;
import io.github.lumine1909.custombiomecolors.integration.WorldEditHandler;
import io.github.lumine1909.custombiomecolors.listener.PlayerListener;
import io.github.lumine1909.custombiomecolors.nms.*;
import io.github.lumine1909.custombiomecolors.util.BStats;
import io.github.lumine1909.custombiomecolors.util.BiomeColorUtil;
import io.github.lumine1909.custombiomecolors.util.UpdateChecker;
import io.github.lumine1909.custombiomecolors.util.object.BiomeColorType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("rawtypes")
public final class CustomBiomeColors extends JavaPlugin {

    private static CustomBiomeColors instance;
    private NmsServer nmsServer;
    private BiomeManager biomeManager;
    private WorldEditHandler worldEditHandler;
    private DataManager dataManager;
    private PacketHandler packetHandler;
    private BStats bStats;

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

    public NmsServer getNmsServer() {
        return this.nmsServer;
    }

    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    public WorldEditHandler getWorldEditHandler() {
        return this.worldEditHandler;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    @Override
    public void onLoad() {
        instance = this;

        int version = obtainVersion();
        if (version >= 2109) {
            nmsServer = new NmsServer_1_21_9();
            packetHandler = new PacketHandler_1_21_9();
        } else if (version >= 2105) {
            nmsServer = new NmsServer_1_21_5();
            packetHandler = new PacketHandler_1_21_5();
        } else if (version >= 2103) {
            nmsServer = new NmsServer_1_21_3();
            packetHandler = new PacketHandler_1_21_3();
        } else if (version >= 2100) {
            nmsServer = new NmsServer_1_21();
            packetHandler = new PacketHandler_1_21();
        } else if (version >= 2005) {
            nmsServer = new NmsServer_1_20_5();
            packetHandler = new PacketHandler_1_20_5();
        } else {
            throw new IllegalStateException("This plugin only support MC 1.20.5 - 1.21.7, for other versions, please contact author at https://github.com/Lumine1909/CustomBiomeColors_Continue/issues");
        }

        this.dataManager = new DataManager("data.json");
        this.dataManager.loadBiomes();
    }

    @Override
    public void onEnable() {
        this.bStats = new BStats(this, 26161);

        this.biomeManager = new BiomeManager();
        this.worldEditHandler = new WorldEditHandler();
        BiomeColorUtil.loadColorMaps();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        registerCommands();


        getPacketHandler().inject();
        new UpdateChecker(this);
    }

    private void registerCommands() {
        new ReloadCommand();
        SetBiomeColorCommand.register(this.getCommand("/setgrasscolor"), "/setgrasscolor", BiomeColorType.GRASS);
        SetBiomeColorCommand.register(this.getCommand("/setfoliagecolor"), "/setfoliagecolor", BiomeColorType.FOLIAGE);
        SetBiomeColorCommand.register(this.getCommand("/setwatercolor"), "/setwatercolor", BiomeColorType.WATER);
        SetBiomeColorCommand.register(this.getCommand("/setwaterfogcolor"), "/setwaterfogcolor", BiomeColorType.WATER_FOG);
        SetBiomeColorCommand.register(this.getCommand("/setskycolor"), "/setskycolor", BiomeColorType.SKY);
        SetBiomeColorCommand.register(this.getCommand("/setfogcolor"), "/setfogcolor", BiomeColorType.FOG);
        if (obtainVersion() >= 2105) {
            Objects.requireNonNull(this.getCommand("/setdryfoliagecolor")).setExecutor(new SetBiomeColorCommand("/setdryfoliagecolor", BiomeColorType.DRY_FOLIAGE));
        } else {
            Objects.requireNonNull(this.getCommand("/setdryfoliagecolor")).setExecutor(new UnsupportedCommand("1.21.5"));
        }
        Objects.requireNonNull(this.getCommand("/getbiomecolors")).setExecutor(new GetBiomeColorsCommand());
    }

    public void callReload(CommandSender sender) {
        sender.sendMessage(Component.text("[CustomBiomeColors] Reload complete", NamedTextColor.GREEN));
    }

    @Override
    public void onDisable() {
        this.bStats.shutdown();
        this.dataManager.saveOnClose();
        getPacketHandler().uninject();
    }
}