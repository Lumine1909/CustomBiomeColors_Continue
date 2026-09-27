package io.github.lumine1909.custombiomecolors;

import io.github.lumine1909.custombiomecolors.command.*;
import io.github.lumine1909.custombiomecolors.data.DataManager;
import io.github.lumine1909.custombiomecolors.integration.WorldEditHandler;
import io.github.lumine1909.custombiomecolors.listener.PlayerListener;
import io.github.lumine1909.custombiomecolors.nms.*;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import io.github.lumine1909.custombiomecolors.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("rawtypes")
public final class CustomBiomeColors extends JavaPlugin {

    private static CustomBiomeColors instance;
    private ServerDataHandler serverDataHandler;
    private BiomeManager biomeManager;
    private WorldEditHandler worldEditHandler;
    private DataManager dataManager;
    private PacketHandler packetHandler;
    private BStats bStats;

    public static CustomBiomeColors getInstance() {
        return instance;
    }

    public ServerDataHandler getServerDataHandler() {
        return this.serverDataHandler;
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

        int version = VersionUtil.obtainVersion();
        ColorType.CURRENT_VERSION = version;
        serverDataHandler = NmsLoader.loadDataHandler(version);
        packetHandler = NmsLoader.loadPacketHandler(version);

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
        //Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        registerCommands();

        getPacketHandler().inject();
        new UpdateChecker(this);
    }

    private void registerCommands() {
        new ReloadCommand();
        new BatchSetBiomeColorCommand();
        new GetBiomeColorsCommand();
        new EditBiomeColorCommand();
        for (ColorType type : ColorType.values()) {
            type.apply(
                colorType -> SetBiomeColorCommand.register(getPluginCommand(colorType), type),
                colorType -> UnsupportedCommand.register(getPluginCommand(colorType), type)
            );
        }
    }

    private PluginCommand getPluginCommand(ColorType colorType) {
        return this.getCommand("/set" + colorType.name().toLowerCase().replaceAll("_", "") + "color");
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